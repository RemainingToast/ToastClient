package toast.client.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toast.client.gui.screens.ToastTitleScreen;


@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {

    int y = this.height / 4 + 48;

    protected MixinTitleScreen(Text text_1) {
        super(text_1);
    }

    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        /*addButton(new ButtonWidget(width / 2 - 124, height / 4 + 96, 20, 20, "TC", button -> {
            assert minecraft != null;
            minecraft.openScreen(new ToastTitleScreen());
        }));*/
    }
}
