package toast.client.lemongui.clickgui.component;

import java.awt.Color;
import java.util.ArrayList;

import toast.client.lemongui.clickgui.component.components.Button;
import toast.client.modules.ModuleManager;
import toast.client.modules.render.ClickGui;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import org.lwjgl.opengl.GL11;

import toast.client.modules.Module;
import toast.client.modules.Module.Category;

public class Frame {

	public ArrayList<Component> components;
	public Category category;
	private boolean open;
	private int width;
	private int y;
	private int x;
	private int barHeight;
	private boolean isDragging;
	public int dragX;
	public int dragY;

	public Frame(Category cat) {
		this.components = new ArrayList<>();
		this.category = cat;
		this.width = 88;
		this.x = 5;
		this.y = 5;
		this.barHeight = 13;
		this.dragX = 0;
		this.open = false;
		this.isDragging = false;
		int tY = this.barHeight;

		for (Module mod : ModuleManager.getModulesInCategory(category)) {
			if(mod == null) return;
			Button modButton = new Button(mod, this, tY);
			this.components.add(modButton);
			tY += 12;
		}
	}

	public ArrayList<Component> getComponents() {
		return components;
	}

	public void setX(int newX) {
		this.x = newX;
	}

	public void setY(int newY) {
		this.y = newY;
	}

	public void setDrag(boolean drag) {
		this.isDragging = drag;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public static int rainbow(final int delay) {
		double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 4L);
		rainbowState %= 360.0;
		return Color.getHSBColor((float) (rainbowState / 360.0), 0.2f, 2f).getRGB();
	}

	public void renderFrame(TextRenderer textRenderer) {
		int color;
		if(ModuleManager.getModule(ClickGui.class).getBool("Rainbow")) {
			color = rainbow(50);
		} else {
			color = new Color(0, 0, 0).getRGB();
		}
		InGameHud.fill(this.x, this.y, this.x + this.width, this.y + this.barHeight, color);
		GL11.glPushMatrix();
		GL11.glScalef(0.5f, 0.5f, 0.5f);
		textRenderer.drawWithShadow(this.category.name(), (this.x + 2) * 2 + 5, (this.y + 2.5f) * 2 + 5,
				0xFFFFFFFF);
		textRenderer.drawWithShadow(this.open ? "-" : "+", (this.x + this.width - 10) * 2 + 5,
				(this.y + 2.5f) * 2 + 5, -1);
		GL11.glPopMatrix();
		if (this.open) {
			if (!this.components.isEmpty()) {
				// Gui.drawRect(this.x, this.y + this.barHeight, this.x + 1, this.y +
				// this.barHeight + (12 * components.size()), new Color(0, 200, 20,
				// 150).getRGB());
				// Gui.drawRect(this.x, this.y + this.barHeight + (12 * components.size()),
				// this.x + this.width, this.y + this.barHeight + (12 * components.size()) + 1,
				// new Color(0, 200, 20, 150).getRGB());
				// Gui.drawRect(this.x + this.width, this.y + this.barHeight, this.x +
				// this.width - 1, this.y + this.barHeight + (12 * components.size()), new
				// Color(0, 200, 20, 150).getRGB());
				for (Component component : components) {
					component.renderComponent();
				}
			}
		}
	}

	public void refresh() {
		int off = this.barHeight;
		for (Component comp : components) {
			comp.setOff(off);
			off += comp.getHeight();
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public void updatePosition(int mouseX, int mouseY) {
		if (this.isDragging) {
			this.setX(mouseX - dragX);
			this.setY(mouseY - dragY);
		}
	}

	public boolean isWithinHeader(int x, int y) {
		return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.barHeight;
	}

    public Category getCategory() {
		return category;
    }
}
