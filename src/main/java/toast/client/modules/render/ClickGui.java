package toast.client.modules.render;

import org.lwjgl.glfw.GLFW;
import toast.client.event.EventImpl;
import toast.client.event.events.player.EventUpdate;
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

    @EventImpl
    public void onUpdate(EventUpdate e){
        if(this.settings.getBoolean("Descriptions")) { ClickGuiScreen.descriptions = true; } else { ClickGuiScreen.descriptions = false; }
    }
}
