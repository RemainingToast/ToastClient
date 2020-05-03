package toast.client.mixin;

import toast.client.gui.hud.HUD;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud2 {
    @Inject(at = @At(value = "RETURN"), method = "render(F)V")
    public void render(float float_1, CallbackInfo info) {
        HUD.drawHUD();
    }
}
