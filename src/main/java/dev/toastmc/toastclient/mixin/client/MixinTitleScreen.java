package dev.toastmc.toastclient.mixin.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.gui.DrawableHelper.drawTexture;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

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
        Identifier TOAST_CLIENT_TEXTURE = new Identifier("toastclient", "title/background.jpeg");
        MinecraftClient.getInstance().getTextureManager().bindTexture(TOAST_CLIENT_TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderSystem.setShaderTexture(0, TOAST_CLIENT_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        assert MinecraftClient.getInstance().currentScreen != null; // Should never be null - java ooga booga
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
//        shader.useShader(width, height, mouseX, mouseY, delta);
    }
}
