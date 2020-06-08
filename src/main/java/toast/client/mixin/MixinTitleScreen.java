package toast.client.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toast.client.utils.RandomMOTD;


@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {

    @Shadow
    private String splashText;

    protected MixinTitleScreen() {
        super(new LiteralText("Yeet"));
    }

    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        /*addButton(new ButtonWidget(width / 2 - 124, height / 4 + 96, 20, 20, "TC", button -> {
            assert minecraft != null;
            minecraft.openScreen(new ToastTitleScreen());
        }));*/
        this.splashText = RandomMOTD.randomMOTD();
    }
}
