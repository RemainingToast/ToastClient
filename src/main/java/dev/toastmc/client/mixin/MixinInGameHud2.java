package dev.toastmc.client.mixin;

import dev.toastmc.client.ToastClient;
import dev.toastmc.client.modules.misc.Panic;
import dev.toastmc.client.modules.render.HUD;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toast.client.modules.render.HUD;

import java.util.Objects;

import static dev.toastmc.client.ToastClient.MODULE_MANAGER;
import static toast.client.modules.misc.Panic.IsPanicking;

@Mixin(InGameHud.class)
public class MixinInGameHud2 {
    @Inject(at = @At(value = "RETURN"), method = "render")
    public void render(MatrixStack matrixStack, float float_1, CallbackInfo info) {
        if (Objects.requireNonNull(ToastClient.MODULE_MANAGER.getModule(HUD.class)).getEnabled() && !Panic.IsPanicking())
            toast.client.gui.hud.HUD.drawHUD();
    }
}
