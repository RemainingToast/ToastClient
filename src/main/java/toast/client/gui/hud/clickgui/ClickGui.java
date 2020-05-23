package toast.client.gui.hud.clickgui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ClickGui extends Screen {
    public ClickGui() {
        super(new LiteralText("ClickGui"));
    }

    public void drawText(String text, int x, int y, int color, float scale) {
        MinecraftClient.getInstance().textRenderer.drawWithShadow(text, x, y, color);
    }

    public void drawRect(int x, int y, int width, int height, int color, float scale) {
        InGameHud.fill(x, y, width, height, color);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        drawRect(20, 20, 100, 5, new Color(0, 0, 0, 0).getRGB(), 1);
        drawRect(20, 50, 100, 5, new Color(0, 0, 0, 50).getRGB(), 1);
        drawRect(20, 80, 100, 5, new Color(0, 0, 0, 100).getRGB(), 1);
        drawRect(20, 110, 100, 5, new Color(0, 0, 0, 150).getRGB(), 1);
        drawRect(20, 140, 100, 5, new Color(0, 0, 0, 200).getRGB(), 1);
        GL11.glPushMatrix();
        drawText("TESTING1", 20, 20, 0x00C400FF, 1);
        drawText("TESTING2", 20, 50, 0xFFC40000, 1);
        drawText("TESTING3", 20, 80, 0x00C4FF00, 1);
        drawText("TESTING4", 20, 110, 0xFFC4FFFF, 1);
        drawText("TESTING5", 20, 140, 0xFFFFFFFF, 1);
        GL11.glPopMatrix();
    }

    public boolean isPauseScreen() {
        return false;
    }
}
