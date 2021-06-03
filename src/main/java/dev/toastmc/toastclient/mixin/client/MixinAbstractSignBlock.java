package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.api.util.CommonsKt;
import dev.toastmc.toastclient.impl.module.misc.ExtraSign;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
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

    @Inject(
            at = {@At("HEAD")},
            method = {"onUse"}
    )
    private void on(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (player.isSneaking() && ExtraSign.INSTANCE.isEnabled()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SignBlockEntity) {
                SignBlockEntity signBlockEntity = (SignBlockEntity) blockEntity;
                Text[] lines = ((ISignBlockEntity) signBlockEntity).getText();
                StringBuilder textToCopy = new StringBuilder();

                for (Text text : lines) {
                    String rowString = text.getString();
                    if (!rowString.isEmpty()) {
                        textToCopy.append(rowString);
                        textToCopy.append(" ");
                    }
                }

                MinecraftClient.getInstance().keyboard.setClipboard(textToCopy.toString().replaceAll("\\s+$", ""));
                player.sendMessage(CommonsKt.lit("Copied sign to clipboard.").formatted(Formatting.GREEN),true);
            }
        }
    }
}
