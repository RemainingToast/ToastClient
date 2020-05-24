package toast.client.gui.hud.clickgui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;

import java.awt.*;

public class ClickGui extends Screen {
    public ClickGui() {
        super(new LiteralText("ClickGui"));
    }

    public void drawText(String text, int x, int y, int color) {
        MinecraftClient.getInstance().textRenderer.drawWithShadow(text, x, y, color);
        RenderSystem.pushMatrix();
        RenderSystem.popMatrix();
    }

    public void drawRect(int x, int y, int width, int height, int color) {
        InGameHud.fill(x, y, x + width, y + height, color);
        RenderSystem.pushMatrix();
        RenderSystem.popMatrix();
    }

    public void drawHollowRect(int x, int y, int width, int height, int lW, int color) {
        drawRect(x-lW, y-lW, width + lW*2, lW, color); // top line
        drawRect(x-lW, y, lW, height, color); // left line
        drawRect(x-lW, y + height, width + lW*2, lW, color); // bottom line
        drawRect(x + width, y, lW, height, color); // right line
    }

    public void drawTextBox(int x, int y, int width, int height, int color, int bgColor, String prefix, String text) {
        drawRect(x-2, y-2, width, height, bgColor);
        drawHollowRect(x-2, y-2, width, height, 1, new Color(0, 0, 0, 255).getRGB());
        drawText(prefix, x, y, new Color(8, 189, 8, 255).getRGB());
        drawText(text, x + MinecraftClient.getInstance().textRenderer.getStringWidth(prefix), y, color);
    }

    public boolean isMouseOverRect(int mouseX, int mouseY, int x, int y, int width, int height) {
        boolean xOver = false;
        boolean yOver = false;
        if (mouseX >= x && mouseX <= width + x) {
            xOver = true;
        }
        if (mouseY >= y && mouseY <= height + y) {
            yOver = true;
        }
        if (xOver && yOver) return true;
        else return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        int onTextColor = new Color(255, 255, 255, 255).getRGB();
        int offTextColor = new Color(177, 177, 177, 255).getRGB();
        int normalBgColor =new Color(0, 0, 0, 64).getRGB();
        int hoverBgColor =new Color(131, 212, 252, 92).getRGB();
        int clickBgColor =new Color(0, 0, 0, 64).getRGB();
        String catPrefix = "> ";
        String modPrefix = " > ";
        int width = 100;
        int height = MinecraftClient.getInstance().textRenderer.getStringBoundedHeight("> A", 100)+3;
        int i = 0;
        for (Module.Category category : Module.Category.values()) {
            int x = 10+(100*i)+(10*i);
            if (isMouseOverRect(mouseX, mouseY, x, 10, width, height)) {
                drawTextBox(x, 10, width, height, onTextColor, hoverBgColor, catPrefix, category.toString());
            } else {
                drawTextBox(x, 10, width, height, onTextColor, normalBgColor, catPrefix, category.toString());
            }
            int u = 1;
            for (Module module : ModuleManager.getModulesInCategory(category)) {
                int y = 10 + u + height * u;
                if (isMouseOverRect(mouseX, mouseY, x, y, width, height)) {
                    drawTextBox(x, y, width, height, offTextColor, hoverBgColor, modPrefix, module.getName());
                } else {
                    drawTextBox(x, y, width, height, offTextColor, normalBgColor, modPrefix, module.getName());
                }
                u++;
            }
            i++;
        }
    }

    public boolean isPauseScreen() {
        return false;
    }
}
