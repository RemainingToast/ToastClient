package toast.client.mixin;

import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SharedConstants.class)
public class MixinSharedConstants {
    /**
     * @author Morgz
     * Makes you able to "chat" § etc (normally "illegal" characters)
     */
    @Overwrite
    public static boolean isValidChar(char chr) {
        return true;
    }
}
