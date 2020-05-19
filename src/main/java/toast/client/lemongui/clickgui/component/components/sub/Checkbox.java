package toast.client.lemongui.clickgui.component.components.sub;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.lwjgl.opengl.GL11;
import toast.client.gui.hud.HUD;
import toast.client.lemongui.clickgui.component.Component;
import toast.client.lemongui.clickgui.component.components.Button;
import toast.client.modules.ModuleManager;
import toast.client.modules.render.ClickGui;
import toast.client.dontobfuscate.settings.CheckSetting;

import java.awt.*;

public class Checkbox extends Component {

	private boolean hovered;
	private String name;
	private CheckSetting op;
	private Button parent;
	private int offset;
	private int x;
	private int y;
	
	public Checkbox(String name, CheckSetting option, Button button, int offset) {
		this.name = name;
		this.op = option;
		this.parent = button;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
	}
	
	public static int rainbow(final int delay) {
		double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 4L);
		rainbowState %= 360.0;
		return Color.getHSBColor((float) (rainbowState / 360.0), 0.2f, 2f).getRGB();
	}

	@Override
	public void renderComponent() {
		int color;
		if(ModuleManager.getModule(ClickGui.class).getBool("Rainbow")) {
			color = HUD.rainbow2(50);
		} else {
			color = new Color(255,255,255,150).getRGB();
		}
		InGameHud.fill(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth()), parent.parent.getY() + offset + 12, this.hovered ? 0xFF222222 : 0xFF111111);
		InGameHud.fill(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, 0xFF111111);
		GL11.glPushMatrix();
		GL11.glScalef(0.5f,0.5f, 0.5f);
		MinecraftClient.getInstance().textRenderer.drawWithShadow(this.name, (parent.parent.getX() + 10 + 4) * 2 + 5, (parent.parent.getY() + offset + 2) * 2 + 4, color);
		GL11.glPopMatrix();
		InGameHud.fill(parent.parent.getX() + 3 + 4, parent.parent.getY() + offset + 3, parent.parent.getX() + 9 + 4, parent.parent.getY() + offset + 9, color);
		if(this.op.getValBoolean())
			InGameHud.fill(parent.parent.getX() + 4 + 4, parent.parent.getY() + offset + 4, parent.parent.getX() + 8 + 4, parent.parent.getY() + offset + 8, 0xFF666666);
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}
	
	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.hovered = isMouseOnButton(mouseX, mouseY);
		this.y = parent.parent.getY() + offset;
		this.x = parent.parent.getX();
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.open) {
			this.op.setValBoolean(!op.getValBoolean());;
		}
	}
	
	public boolean isMouseOnButton(int x, int y) {
		if(x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12) {
			return true;
		}
		return false;
	}
}
