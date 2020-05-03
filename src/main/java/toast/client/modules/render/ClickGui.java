package toast.client.modules.render;

import toast.client.modules.Module;
import org.lwjgl.glfw.GLFW;

/**
 * Made by HeroCode & xTrM_ it's free to use but you have to credit us
 *
 * @author HeroCode
 */
public class ClickGui extends Module {

	public toast.client.lemongui.clickgui.ClickGui clickgui;

	public ClickGui() {
		super("ClickGUI", Category.RENDER, GLFW.GLFW_KEY_RIGHT_SHIFT);
		this.addBool("Rainbow", true);
	}

	@Override
	public void onEnable() {
		if (this.clickgui == null)
			this.clickgui = new toast.client.lemongui.clickgui.ClickGui();
		if(mc.player == null) return;
		mc.openScreen(this.clickgui);
		toggle();
	}

	@Override
	public void onDisable() {

	}
}
