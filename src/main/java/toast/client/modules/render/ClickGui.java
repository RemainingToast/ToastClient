package toast.client.modules.render;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.glfw.GLFW;
import toast.client.events.player.EventUpdate;
import toast.client.gui.clickgui.ClickGuiScreen;
import toast.client.modules.Module;

import static toast.client.ToastClient.clickGui;
import static toast.client.ToastClient.clickGuiHasOpened;

public class ClickGui extends Module {
    public boolean Description;


    public ClickGui() {
        super("ClickGui", "The gui for managing modules.", Category.RENDER, GLFW.GLFW_KEY_RIGHT_SHIFT);
        this.settings.addBoolean("Descriptions", true);

    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            if (clickGui == null) {
                clickGuiHasOpened = false;
                clickGui = new ClickGuiScreen();
            }
            if (mc.currentScreen == null) {
                mc.openScreen(clickGui);
                clickGuiHasOpened = true;
            }
        }
    }

    @Override
    public void onDisable() {
        if (mc.currentScreen instanceof ClickGuiScreen && mc.player != null) {
            mc.openScreen(null);
        }
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        ClickGuiScreen.descriptions = this.settings.getBoolean("Descriptions");
    }
}
