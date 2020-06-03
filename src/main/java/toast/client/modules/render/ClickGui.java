package toast.client.modules.render;

import org.lwjgl.glfw.GLFW;
import sun.security.krb5.internal.crypto.Des;
import toast.client.gui.clickgui.ClickGuiScreen;
import toast.client.modules.Module;

import static toast.client.ToastClient.clickGui;
import static toast.client.ToastClient.clickGuiHasOpened;

public class ClickGui extends Module {
    public boolean Description;
    public boolean opened;

    public ClickGui() {
        super("ClickGui", "The gui for managing modules.", Category.RENDER, GLFW.GLFW_KEY_RIGHT_SHIFT);
        this.settings.addBoolean("Descriptions", true);
    }

    @Override
    public void onEnable() {
        if(this.settings.getBoolean("Descriptions")) {
            ClickGuiScreen.descriptions = true;
        } else {
            ClickGuiScreen.descriptions = false;
        }
        if (mc.player != null) {
            if (clickGui == null) {
                clickGuiHasOpened = false;
                clickGui = new ClickGuiScreen();
            }
            if (mc.currentScreen == null) {
                mc.openScreen(clickGui);
                clickGuiHasOpened = true;
                opened = true;
            }
            if(mc.currentScreen instanceof ClickGuiScreen){
                opened = !opened;
            }
        }
    }

    @Override
    public void onDisable() {
        if (mc.currentScreen instanceof ClickGuiScreen && mc.player != null) {
            mc.openScreen(null);
        }
    }
}
