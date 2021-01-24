package me.remainingtoast.toastclient.mixin.client;

import me.remainingtoast.toastclient.api.util.dummy.DummyClientPlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    @Inject(method = "render", at = @At("RETURN"))
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        TitleScreen sc = (TitleScreen) (Object) this;
        if (MinecraftClient.getInstance() != null) {
            ClientPlayerEntity player = DummyClientPlayerEntity.getInstance();
            int height = sc.height / 4 + 48;
            int playerX = sc.width / 2 + 175;
            if(player != null) InventoryScreen.drawEntity(playerX, height + 99, 60, -mouseX + playerX, -mouseY + height - 30, player);
        }
    }
}
