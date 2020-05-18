package toast.client.lemongui.clickgui.component.components.sub;

import toast.client.gui.hud.HUD;
import toast.client.lemongui.clickgui.component.Component;
import toast.client.lemongui.clickgui.component.components.Button;
import toast.client.modules.ModuleManager;
import toast.client.modules.render.ClickGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Keybind extends Component {

	private boolean hovered;
	private boolean binding;
	private Button parent;
	private int offset;
	private int x;
	private int y;

	public Keybind(Button button, int offset) {
		this.parent = button;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
	}

	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}

	@Override
	public void renderComponent() {
		InGameHud.fill(parent.parent.getX() + 2, parent.parent.getY() + offset,
				parent.parent.getX() + (parent.parent.getWidth()), parent.parent.getY() + offset + 12,
				this.hovered ? 0xFF222222 : 0xFF111111);
		InGameHud.fill(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2,
				parent.parent.getY() + offset + 12, 0xFF111111);
		GL11.glPushMatrix();
		GL11.glScalef(0.5f, 0.5f, 0.5f);
		int color;
		if(ModuleManager.getModule(ClickGui.class).getBool("Rainbow")) {
			color = HUD.rainbow2(50);
		} else {
			color = new Color(255,255,255,150).getRGB();
		}
		MinecraftClient.getInstance().textRenderer.drawWithShadow( //TODO: GLFW.glfwGetKeyName seems to return null sometimes dont know why problaly key is not in the "list"
				binding ? "Press a key..." : ("Key: " + GLFW.glfwGetKeyName(this.parent.mod.getKey(), -1)),
				(parent.parent.getX() + 7) * 2, (parent.parent.getY() + offset + 2) * 2 + 5,
				color);
		GL11.glPopMatrix();
	}

	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.hovered = isMouseOnButton(mouseX, mouseY);
		this.y = parent.parent.getY() + offset;
		this.x = parent.parent.getX();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.open) {
			this.binding = !this.binding;
		}
	}

	@Override
	public void keyTyped(String typedString, int key) {
		if (this.binding) {
			this.parent.mod.setKey(key);
			this.binding = false;
		}
	}

	public boolean isMouseOnButton(int x, int y) {
		if (x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12) {
			return true;
		}
		return false;
	}
}
