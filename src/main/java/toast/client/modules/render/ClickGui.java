package toast.client.modules.render;

import org.lwjgl.glfw.GLFW;
import toast.client.modules.Module;

public class ClickGui extends Module {
    public ClickGui() {
        super("ClickGuiScreen", Category.RENDER, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }
}
