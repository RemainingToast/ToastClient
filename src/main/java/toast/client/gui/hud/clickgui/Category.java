package toast.client.gui.hud.clickgui;

import net.minecraft.client.MinecraftClient;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;

import static toast.client.gui.hud.clickgui.ClickGui.*;

public class Category {
    public Category(int x, int catY, int mouseX, int mouseY, int boxWidth, int boxHeight, Module.Category category) {
        if (isMouseOverRect(mouseX, mouseY, x, catY, boxWidth, boxHeight)) {
            drawTextBox(x, catY, boxWidth, boxHeight, onTextColor, hoverBgColor, catPrefix, category.toString());
        } else {
            drawTextBox(x, catY, boxWidth, boxHeight, onTextColor, normalBgColor, catPrefix, category.toString());
        }
        int u = 1;
        for (Module module : ModuleManager.getModulesInCategory(category)) {
            int y = catY + u + boxHeight * u;
            if (isMouseOverRect(mouseX, mouseY, x, y, boxWidth, boxHeight)) {
                drawTextBox(x, y, boxWidth, boxHeight, offTextColor, hoverBgColor, modPrefix, module.getName());
                String description = module.getCategory().toString();
                drawTextBox(x+boxWidth, y, MinecraftClient.getInstance().textRenderer.getStringWidth(description) + 4, boxHeight, onTextColor, descriptionBgColor, "", description);
            } else {
                drawTextBox(x, y, boxWidth, boxHeight, offTextColor, normalBgColor, modPrefix, module.getName());
            }
            u++;
        }
    }
}
