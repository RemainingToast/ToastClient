package toast.client.lemongui.clickgui.component.components;

import toast.client.lemongui.clickgui.component.components.sub.Checkbox;
import toast.client.lemongui.clickgui.component.components.sub.Keybind;
import toast.client.lemongui.clickgui.component.components.sub.ModeButton;
import toast.client.lemongui.clickgui.component.components.sub.Slider;
import toast.client.lemongui.settings.Setting;
import toast.client.lemongui.clickgui.component.Frame;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;
import toast.client.modules.render.ClickGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class Button extends toast.client.lemongui.clickgui.component.Component {

	public Module mod;
	public Frame parent;
	public int offset;
	private boolean isHovered;
	private ArrayList<toast.client.lemongui.clickgui.component.Component> subcomponents;
	public boolean open;
	private int height;
	
	public Button(Module mod, Frame parent, int offset) {
		this.mod = mod;
		this.parent = parent;
		this.offset = offset;
		this.subcomponents = new ArrayList<>();
		this.open = false;
		height = 12;
		int opY = offset + 12;
		if(ModuleManager.setmgr.getSettingsByMod(mod) != null) {
			for(Setting s : ModuleManager.setmgr.getSettingsByMod(mod)){
				if(s.isCombo()){
					this.subcomponents.add(new ModeButton(s, this, mod, opY));
					opY += 12;
				}
				if(s.isSlider()){
					this.subcomponents.add(new Slider(s, this, opY));
					opY += 12;
				}
				if(s.isCheck()){
					this.subcomponents.add(new Checkbox(s, this, opY));
					opY += 12;
				}
			}
		}
		this.subcomponents.add(new Keybind(this, opY));
	}
	
	public static int rainbow(final int delay) {
		double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 4L);
		rainbowState %= 360.0;
		return Color.getHSBColor((float) (rainbowState / 360.0), 0.2f, 2f).getRGB();
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
		int opY = offset + 12;
		for(toast.client.lemongui.clickgui.component.Component comp : this.subcomponents) {
			comp.setOff(opY);
			opY += 12;
		}
	}
	
	@Override
	public void renderComponent() {
		InGameHud.fill(parent.getX(), this.parent.getY() + this.offset, parent.getX() + parent.getWidth(), this.parent.getY() + 12 + this.offset, this.isHovered ? (this.mod.isEnabled() ? new Color(0xFF222222).darker().getRGB() : 0xFF222222) : (this.mod.isEnabled() ? new Color(14,14,14).getRGB() : 0xFF111111));
		GL11.glPushMatrix();
		GL11.glScalef(0.5f,0.5f, 0.5f);
		MinecraftClient.getInstance().textRenderer.drawWithShadow(this.mod.getName(), (parent.getX() + 2) * 2, (parent.getY() + offset + 2) * 2 + 4, this.mod.isEnabled() ? 0x999999 : -1);
		if(this.subcomponents.size() > 2)
		MinecraftClient.getInstance().textRenderer.drawWithShadow(this.open ? "-" : "+", (parent.getX() + parent.getWidth() - 10) * 2, (parent.getY() + offset + 2) * 2 + 4, -1);
		GL11.glPopMatrix();
		if(this.open) {
			if(!this.subcomponents.isEmpty()) {
				for(toast.client.lemongui.clickgui.component.Component comp : this.subcomponents) {
					comp.renderComponent();
				}
				int color;
				if(ModuleManager.getModule(ClickGui.class).getBool("Rainbow")) {
					color = rainbow(50);
				} else {
					color = new Color(0,0,0,255).getRGB();
				}
				InGameHud.fill(parent.getX() + 2, parent.getY() + this.offset + 12, parent.getX() + 3, parent.getY() + this.offset + ((this.subcomponents.size() + 1) * 12), color);
			}
		}
	}
	
	@Override
	public int getHeight() {
		if(this.open) {
			return (12 * (this.subcomponents.size() + 1));
		}
		return 12;
	}
	
	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.isHovered = isMouseOnButton(mouseX, mouseY);
		if(!this.subcomponents.isEmpty()) {
			for(toast.client.lemongui.clickgui.component.Component comp : this.subcomponents) {
				comp.updateComponent(mouseX, mouseY);
			}
		}
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(isMouseOnButton(mouseX, mouseY) && button == 0) {
			this.mod.toggle();
		}
		if(isMouseOnButton(mouseX, mouseY) && button == 1) {
			this.open = !this.open;
			this.parent.refresh();
		}
		for(toast.client.lemongui.clickgui.component.Component comp : this.subcomponents) {
			comp.mouseClicked(mouseX, mouseY, button);
		}
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		for(toast.client.lemongui.clickgui.component.Component comp : this.subcomponents) {
			comp.mouseReleased(mouseX, mouseY, mouseButton);
		}
	}
	
	@Override
	public void keyTyped(String typedString, int key) {
		for(toast.client.lemongui.clickgui.component.Component comp : this.subcomponents) {
			comp.keyTyped(typedString, key);
		}
	}
	
	public boolean isMouseOnButton(int x, int y) {
		return x > parent.getX() && x < parent.getX() + parent.getWidth() && y > this.parent.getY() + this.offset && y < this.parent.getY() + 12 + this.offset;
	}
}
