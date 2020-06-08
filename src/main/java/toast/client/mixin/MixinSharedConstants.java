package toast.client.mixin;

import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SharedConstants.class)
public class MixinSharedConstants {
    /**
     * @author Morgz
     * Makes you able to "chat" § etc (normally "illegal" characters)
     */
    @Inject(method = "isValidChar", at = @At(value = "HEAD"), cancellable = true)
    private static void isValidChar(CallbackInfoReturnable info) {
        info.setReturnValue(true);
    }
}
