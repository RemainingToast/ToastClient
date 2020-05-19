package toast.client.lemongui.clickgui.component.components.sub;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.lwjgl.opengl.GL11;
import toast.client.lemongui.clickgui.component.Component;
import toast.client.lemongui.clickgui.component.components.Button;
import toast.client.modules.ModuleManager;
import toast.client.modules.render.ClickGui;
import toast.client.dontobfuscate.settings.SliderSetting;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Slider extends Component {

	private boolean hovered;

	private String name;
	private SliderSetting set;
	private Button parent;
	private int offset;
	private int x;
	private int y;
	private boolean dragging = false;

	private double renderWidth;
	
	public Slider(String name, SliderSetting value, Button button, int offset) {
		this.name = name;
		this.set = value;
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
		InGameHud.fill(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + parent.parent.getWidth(), parent.parent.getY() + offset + 12, this.hovered ? 0xFF222222 : 0xFF111111);
		 final int drag = (int)(this.set.getValDouble() / this.set.getMax() * this.parent.parent.getWidth());
		InGameHud.fill(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (int) renderWidth, parent.parent.getY() + offset + 12,hovered ? 0xFF555555 : 0xFF444444);
		InGameHud.fill(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, 0xFF111111);
		GL11.glPushMatrix();
		GL11.glScalef(0.5f,0.5f, 0.5f);
		int color;
		if(ModuleManager.getModule(ClickGui.class).getBool("Rainbow")) {
			color = rainbow(50);
		} else {
			color = new Color(255,255,255,150).getRGB();
		}
		MinecraftClient.getInstance().textRenderer.drawWithShadow(this.name + ": " + this.set.getValDouble() , (parent.parent.getX()* 2 + 15), (parent.parent.getY() + offset + 2) * 2 + 5, color);
		
		GL11.glPopMatrix();
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}
	
	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.hovered = isMouseOnButtonD(mouseX, mouseY) || isMouseOnButtonI(mouseX, mouseY);
		this.y = parent.parent.getY() + offset;
		this.x = parent.parent.getX();
		
		double diff = Math.min(88, Math.max(0, mouseX - this.x));

		double min = set.getMin();
		double max = set.getMax();
		
		renderWidth = (88) * (set.getValDouble() - min) / (max - min);
		
		if (dragging) {
			if (diff == 0) {
				set.setValDouble(set.getMin());
			}
			else {
				double newValue = roundToPlace(((diff / 88) * (max - min) + min), 2);
				set.setValDouble(newValue);
			}
		}
	}
	
	private static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(isMouseOnButtonD(mouseX, mouseY) && button == 0 && this.parent.open) {
			dragging = true;
		}
		if(isMouseOnButtonI(mouseX, mouseY) && button == 0 && this.parent.open) {
			dragging = true;
		}
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		dragging = false;
	}
	
	public boolean isMouseOnButtonD(int x, int y) {
		if(x > this.x && x < this.x + (parent.parent.getWidth() / 2 + 1) && y > this.y && y < this.y + 12) {
			return true;
		}
		return false;
	}
	
	public boolean isMouseOnButtonI(int x, int y) {
		if(x > this.x + parent.parent.getWidth() / 2 && x < this.x + parent.parent.getWidth() && y > this.y && y < this.y + 12) {
			return true;
		}
		return false;
	}
}
