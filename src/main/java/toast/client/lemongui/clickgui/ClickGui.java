package toast.client.lemongui.clickgui;

import toast.client.lemongui.clickgui.component.Component;
import toast.client.lemongui.clickgui.screen.AbstractScreen;
import toast.client.lemongui.clickgui.component.Frame;
import toast.client.modules.Module;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class ClickGui extends AbstractScreen {

	public static ArrayList<Frame> frames;

	public ClickGui() {
		super(new LiteralText("ClickGui"));
		frames = new ArrayList<>();
		int frameX = 5;
		for (Module.Category category : Module.Category.values()) {
			if(category == null) return;
			Frame frame = new Frame(category);
			frame.setX(frameX);
			frames.add(frame);
			frameX += frame.getWidth() + 1;
		}
	}

	@Override
	public void init() {
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.renderBackground();
		for (Frame frame : frames) {
			frame.renderFrame(this.font);
			frame.updatePosition(mouseX, mouseY);
			for (Component comp : frame.getComponents()) {
				comp.updateComponent(mouseX, mouseY);
			}
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (Frame frame : frames) {
			if (frame.isWithinHeader((int) mouseX, (int) mouseY) && button == 0) {
				frame.setDrag(true);
				frame.dragX = (int)  mouseX - frame.getX();
				frame.dragY = (int) mouseY - frame.getY();
			}
			if (frame.isWithinHeader((int) mouseX, (int) mouseY) && button == 1) {
				frame.setOpen(!frame.isOpen());
			}
			if (frame.isOpen()) {
				if (!frame.getComponents().isEmpty()) {
					for (Component component : frame.getComponents()) {
						component.mouseClicked((int) mouseX, (int) mouseY, button);
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		for (Frame frame : frames) {
			if (frame.isOpen() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
				if (!frame.getComponents().isEmpty()) {
					for (Component component : frame.getComponents()) {
						component.keyTyped(GLFW.glfwGetKeyName(keyCode, scanCode), keyCode);
					}
				}
			}
		}
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.minecraft.openScreen(null);
		}
		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		for (Frame frame : frames) {
			frame.setDrag(false);
		}
		for (Frame frame : frames) {
			if (frame.isOpen()) {
				if (!frame.getComponents().isEmpty()) {
					for (Component component : frame.getComponents()) {
						component.mouseReleased((int) mouseX, (int) mouseY, button);
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean isPauseScreen() {
		return true;
	}
}
