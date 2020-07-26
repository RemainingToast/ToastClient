package dev.toastmc.client.mixin;

import dev.toastmc.client.utils.RandomMOTD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {

    @Shadow
    private String splashText;
    private final MinecraftClient minecraft = MinecraftClient.getInstance();
    protected MixinTitleScreen() {
        super(new LiteralText("Yeet"));
    }

    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        addButton(new ButtonWidget(width / 2 - 124, height / 4 + 96, 20, 20, new LiteralText("Capes"), button -> {
            assert minecraft != null;
        }));
        this.splashText = RandomMOTD.randomMOTD();
    }
}
