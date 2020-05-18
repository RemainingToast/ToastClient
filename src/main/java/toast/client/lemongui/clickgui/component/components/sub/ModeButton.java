package toast.client.lemongui.clickgui.component.components.sub;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.lwjgl.opengl.GL11;
import toast.client.gui.hud.HUD;
import toast.client.lemongui.clickgui.component.Component;
import toast.client.lemongui.clickgui.component.components.Button;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;
import toast.client.modules.render.ClickGui;
import toast.client.lemongui.clickgui.settings.ComboSetting;

import java.awt.*;


public class ModeButton extends Component {

	private boolean hovered;
	private Button parent;
	private ComboSetting set;
	private int offset;
	private int x;
	private int y;
	private Module mod;

	private int modeIndex;

	public ModeButton(ComboSetting set, Button button, Module mod, int offset) {
		this.set = set;
		this.parent = button;
		this.mod = mod;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
		this.modeIndex = 0;
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
		MinecraftClient.getInstance().textRenderer.drawWithShadow("Mode: " + set.getValString(),
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
			int maxIndex = set.getOptions().size();

			if (modeIndex + 1 > maxIndex - 1)
				modeIndex = 0;
			else
				modeIndex++;

			set.setValString(set.getOptions().get(modeIndex));
		}
	}

	public boolean isMouseOnButton(int x, int y) {
		return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12;
	}
}
