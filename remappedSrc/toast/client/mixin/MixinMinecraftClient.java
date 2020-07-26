package toast.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toast.client.ToastClient;
import toast.client.events.network.EventSyncedUpdate;
import toast.client.events.player.EventUpdate;
import toast.client.modules.misc.Panic;
import toast.client.modules.misc.TPSSync;
import toast.client.utils.RandomMOTD;
import toast.client.utils.TPSCalculator;

import javax.annotation.Nullable;

import java.util.Objects;

import static toast.client.ToastClient.MODULE_MANAGER;
import static toast.client.ToastClient.eventBus;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow private int itemUseCooldown;

    @Shadow @Final private TutorialManager tutorialManager;

    @Shadow protected abstract void doAttack();

    @Shadow protected abstract void doItemUse();

    @Shadow protected abstract void doItemPick();

    @Shadow protected abstract void handleBlockBreaking(boolean bl);

    @Shadow @Final public GameOptions options;

    @Shadow @Final public WorldRenderer worldRenderer;

    @Shadow @Final public GameRenderer gameRenderer;

    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow @Final public InGameHud inGameHud;

    @Shadow @Nullable public Screen currentScreen;

    @Shadow @Nullable public ClientPlayerInteractionManager interactionManager;

    @Shadow public abstract void openScreen(@Nullable Screen screen);

    @Shadow @Nullable public abstract ClientPlayNetworkHandler getNetworkHandler();

    @Shadow @Nullable public Overlay overlay;

    @Shadow @Final public Mouse mouse;

    @Shadow @Nullable public abstract Entity getCameraEntity();

    @Shadow @Nullable public HitResult crosshairTarget;

    @Shadow @Final private static Logger LOGGER;

    @Shadow @Nullable public ClientWorld world;

    @Shadow protected int attackCooldown;

    @Shadow @Final public ParticleManager particleManager;

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void tick(CallbackInfo ci) {
        EventUpdate event = new EventUpdate();
        eventBus.post(event);
        if (!Objects.requireNonNull(MODULE_MANAGER.getModule(TPSSync.class)).getEnabled() || TPSCalculator.Companion.getTps() == 20.0) {
            EventSyncedUpdate event2 = new EventSyncedUpdate();
            eventBus.post(event2);
        }
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "getWindowTitle", at = @At(value = "RETURN"), cancellable = true)
    private void getWindowTitle(CallbackInfoReturnable cir) {
        if (!Panic.IsPanicking()) {
            cir.setReturnValue(ToastClient.cleanPrefix + " " + ToastClient.version + " | " + RandomMOTD.randomMOTD());
        }
    }

    @Inject(method = "handleInputEvents", at = @At(value = "INVOKE"), cancellable = true)
    private void handleInputEvents(CallbackInfo ci) {
        for(; options.keyTogglePerspective.wasPressed(); worldRenderer.scheduleTerrainUpdate()) {
            ++options.perspective;
            if (options.perspective > 2) {
                options.perspective = 0;
            }

            if (options.perspective == 0) {
                gameRenderer.onCameraEntitySet(getCameraEntity());
            } else if (options.perspective == 1) {
                gameRenderer.onCameraEntitySet((Entity)null);
            }
        }

        while(options.keySmoothCamera.wasPressed()) {
            options.smoothCameraEnabled = !options.smoothCameraEnabled;
        }

        for(int i = 0; i < 9; ++i) {
            boolean bl = options.keySaveToolbarActivator.isPressed();
            boolean bl2 = options.keyLoadToolbarActivator.isPressed();
            if (options.keysHotbar[i].wasPressed()) {
                if (player.isSpectator()) {
                    inGameHud.getSpectatorHud().selectSlot(i);
                } else if (!player.isCreative() || currentScreen != null || !bl2 && !bl) {
                    player.inventory.selectedSlot = i;
                } else {
                    CreativeInventoryScreen.onHotbarKeyPress(MinecraftClient.getInstance(), i, bl2, bl);
                }
            }
        }

        while(options.keyInventory.wasPressed()) {
            if (interactionManager.hasRidingInventory()) {
                player.openRidingInventory();
            } else {
                tutorialManager.onInventoryOpened();
                openScreen(new InventoryScreen(player));
            }
        }

        while(options.keyAdvancements.wasPressed()) {
            openScreen(new AdvancementsScreen(player.networkHandler.getAdvancementHandler()));
        }

        while(options.keySwapHands.wasPressed()) {
            if (!player.isSpectator()) {
                getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_HELD_ITEMS, BlockPos.ORIGIN, Direction.DOWN));
            }
        }

        while(options.keyDrop.wasPressed()) {
            if (!player.isSpectator() && player.dropSelectedItem(Screen.hasControlDown())) {
                player.swingHand(Hand.MAIN_HAND);
            }
        }

        boolean bl3 = options.chatVisibility != ChatVisibility.HIDDEN;
        if (bl3) {
            while(options.keyChat.wasPressed()) {
                openScreen(new ChatScreen(""));
            }

            if (currentScreen == null && overlay == null && options.keyCommand.wasPressed()) {
                openScreen(new ChatScreen("/"));
            }
        }

        if (player.isUsingItem()) {
            if (!options.keyUse.isPressed()) {
                interactionManager.stopUsingItem(player);
            }

            /*label113:
            while(true) {
                if (!options.keyAttack.wasPressed()) {
                    while(true) {
                        if (options.keyPickItem.wasPressed()) {
                            continue;
                        }
                        break label113;
                    }
                }
            }*/
        }

        while(options.keyAttack.wasPressed()) {
            doAttack();
        }

        while(options.keyUse.wasPressed()) {
            doItemUse();
        }

        while(options.keyPickItem.wasPressed()) {
            doItemPick();
        }

        if (options.keyUse.isPressed() && itemUseCooldown == 0 && !player.isUsingItem()) {
            doItemUse();
        }

        handleBlockBreaking(currentScreen == null && options.keyAttack.isPressed() && mouse.isCursorLocked());

        ci.cancel();
    }
    
    @Inject(method = "doItemUse", at = @At(value = "INVOKE"), cancellable = true)
    public void doItemUse(CallbackInfo ci) {
        itemUseCooldown = 4;
        if (!player.isRiding()) {
            if (crosshairTarget == null) {
                LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
            }

            Hand[] var1 = Hand.values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                Hand hand = var1[var3];
                ItemStack itemStack = player.getStackInHand(hand);
                if (crosshairTarget != null) {
                    switch(crosshairTarget.getType()) {
                        case ENTITY:
                            EntityHitResult entityHitResult = (EntityHitResult)crosshairTarget;
                            Entity entity = entityHitResult.getEntity();
                            ActionResult actionResult = interactionManager.interactEntityAtLocation(player, entity, entityHitResult, hand);
                            if (!actionResult.isAccepted()) {
                                actionResult = interactionManager.interactEntity(player, entity, hand);
                            }

                            if (actionResult.isAccepted()) {
                                if (actionResult.shouldSwingHand()) {
                                    player.swingHand(hand);
                                }

                                return;
                            }
                            break;
                        case BLOCK:
                            BlockHitResult blockHitResult = (BlockHitResult)crosshairTarget;
                            int i = itemStack.getCount();
                            ActionResult actionResult2 = interactionManager.interactBlock(player, world, hand, blockHitResult);
                            if (actionResult2.isAccepted()) {
                                if (actionResult2.shouldSwingHand()) {
                                    player.swingHand(hand);
                                    if (!itemStack.isEmpty() && (itemStack.getCount() != i || interactionManager.hasCreativeInventory())) {
                                        gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                                    }
                                }

                                return;
                            }

                            if (actionResult2 == ActionResult.FAIL) {
                                return;
                            }
                    }
                }

                if (!itemStack.isEmpty()) {
                    ActionResult actionResult3 = interactionManager.interactItem(player, world, hand);
                    if (actionResult3.isAccepted()) {
                        if (actionResult3.shouldSwingHand()) {
                            player.swingHand(hand);
                        }

                        gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                        return;
                    }
                }
            }

        }
        ci.cancel();
    }
    
    @Inject(method = "handleBlockBreaking", at = @At(value = "INVOKE"), cancellable = true)
    public void handleBlockBreaking(boolean bl, CallbackInfo ci) {
        if (!bl) {
            attackCooldown = 0;
        }

        if (attackCooldown <= 0) {
            if (bl && crosshairTarget != null && crosshairTarget.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult)crosshairTarget;
                BlockPos blockPos = blockHitResult.getBlockPos();
                if (!world.getBlockState(blockPos).isAir()) {
                    Direction direction = blockHitResult.getSide();
                    if (interactionManager.updateBlockBreakingProgress(blockPos, direction)) {
                        particleManager.addBlockBreakingParticles(blockPos, direction);
                        player.swingHand(Hand.MAIN_HAND);
                    }
                }

            } else {
                interactionManager.cancelBlockBreaking();
            }
        }
        ci.cancel();
    }

}
