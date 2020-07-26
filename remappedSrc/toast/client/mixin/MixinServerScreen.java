package toast.client.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toast.client.gui.screens.altmanager.AltManagerScreen;

@Mixin(MultiplayerScreen.class)
public class MixinServerScreen extends Screen {


    protected MixinServerScreen(Text text_1) {
        super(text_1);
    }

    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        addButton(new ButtonWidget(7, 7, 125, 20, "Account Manager", button -> {
            minecraft.openScreen(new AltManagerScreen());
        }));

    }
}
