package dev.toastmc.toastclient.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.toastmc.toastclient.api.util.GLSLSandboxShader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.gui.DrawableHelper.drawTexture;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    @Mutable
    @Shadow @Final private static Identifier EDITION_TITLE_TEXTURE;

    @Inject(
            at = {@At("RETURN")},
            method = {"render"}
    )
    private void on(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        EDITION_TITLE_TEXTURE = new Identifier("toastclient", "title/edition.png");
    }

    @Redirect(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/RotatingCubeMapRenderer;render(FF)V"),
            method = {"render"}
    )
    private void on(RotatingCubeMapRenderer rotatingCubeMapRenderer, float delta, float alpha){ }

    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/RotatingCubeMapRenderer;render(FF)V"),
            method = {"render"}
    )
    private void on1(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci){
        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("toastclient", "title/background.png"));
        assert MinecraftClient.getInstance().currentScreen != null; // Shouldn't ever be null, as we are literally rendering screen
        int width = MinecraftClient.getInstance().currentScreen.width;
        int height = MinecraftClient.getInstance().currentScreen.height;
        drawTexture(
                matrices,
                0,
                0,
                width,
                height,
                0.0F,
                0.0F,
                width,
                height,
                width,
                height
        );
//        TODO: Fix Shaders
//        GLSLSandboxShader shader = GLSLSandboxShader.mandelbrotShader();
//        if(shader != null) shader.useShader(width, height, mouseX, mouseY, delta);
    }
}
