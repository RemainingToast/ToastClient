package toast.client.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toast.client.modules.render.HUD;

import java.util.Objects;

import static toast.client.ToastClient.MODULE_MANAGER;
import static toast.client.modules.misc.Panic.IsPanicking;

@Mixin(InGameHud.class)
public class MixinInGameHud2 {
    @Inject(at = @At(value = "RETURN"), method = "render(F)V")
    public void render(float float_1, CallbackInfo info) {
        if (Objects.requireNonNull(MODULE_MANAGER.getModule(HUD.class)).getEnabled() && !IsPanicking())
            toast.client.gui.hud.HUD.drawHUD();
    }
}
