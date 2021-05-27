package dev.toastmc.toastclient.mixin.client;

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

@Mixin(AbstractSignBlock.class)
public class MixinAbstractSignBlock {


    @Inject(method = "onUse", at = @At("HEAD"))
    public void onSignUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (player.isSneaking()) {
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
                player.sendMessage(new LiteralText("Sign copied.").formatted(Formatting.GREEN),false);
            }
        }
    }
}
