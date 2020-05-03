package toast.client.modules.render;

import toast.client.modules.Module;

public class HUD extends Module {
    public HUD() {
        super("HUD", Category.RENDER, -1);
        this.addBool("Rainbow", true);
        this.addBool("Watermark", true);
        this.addBool("\"SortedSet\"", true);
        this.addBool("Right line", true);
        this.addBool("Left line", true);
        this.addBool("Middle line", false);
    }
}
