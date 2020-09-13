package dev.toastmc.client.mixin.client;

import dev.toastmc.client.ToastClient;
import dev.toastmc.client.module.Module;
import dev.toastmc.client.module.misc.SignCopy;
import dev.toastmc.client.util.MessageUtil;
import dev.toastmc.client.util.UtilKt;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(AbstractSignBlock.class)
public class MixinAbstractSignBlock {

    private static Module mod = ToastClient.Companion.getMODULE_MANAGER().getModuleByClass(SignCopy.class);

    @Inject(method = "onUse", at = @At("HEAD"))
    public void onSignUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if(mod != null){
            if (player.isSneaking() && mod.getEnabled()) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof SignBlockEntity) {
                    SignBlockEntity signBlockEntity = (SignBlockEntity) blockEntity;
                    Text[] lines = ((ISignBlockEntity)signBlockEntity).getText();
                    StringBuilder textToCopy = new StringBuilder();
                    for (Text text : lines) {
                        String rowString = text.getString();
                        if (!rowString.isEmpty()) {
                            textToCopy.append(rowString);
                            textToCopy.append("\n");
                        }
                    }
                    MinecraftClient.getInstance().keyboard.setClipboard(textToCopy.toString());
                    player.sendMessage(new LiteralText("Sign copied.").formatted(Formatting.GREEN), true);
                    MessageUtil.INSTANCE.sendMessage("Sign copied.", MessageUtil.Color.GREEN);
                }
            }
        }
    }
}
