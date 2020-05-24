package toast.client.gui.hud.clickgui;

import toast.client.dontobfuscate.CategorySetting;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;

import static toast.client.gui.hud.clickgui.ClickGui.*;

public class Category {
    public boolean hasDesc = false;
    public int descPosX = 0;
    public int descPosY = 0;
    public String desc = "";
    public Category(int mouseX, int mouseY, int boxWidth, int boxHeight, Module.Category category) {
        CategorySetting settings = ClickGui.getSettings(category);
        int x = settings.getPosX();
        int catY = settings.getPosY();
        if (isMouseOverRect(mouseX, mouseY, x, catY, boxWidth, boxHeight)) {
            drawTextBox(x, catY, boxWidth, boxHeight, onTextColor, hoverBgColor, catPrefix, category.toString());
        } else {
            drawTextBox(x, catY, boxWidth, boxHeight, onTextColor, normalBgColor, catPrefix, category.toString());
        }
        if (settings.isExpanded()) {
            int u = 1;
            for (Module module : ModuleManager.getModulesInCategory(category)) {
                int y = catY + u + boxHeight * u;
                if (isMouseOverRect(mouseX, mouseY, x, y, boxWidth, boxHeight)) {
                    drawTextBox(x, y, boxWidth, boxHeight, offTextColor, hoverBgColor, modPrefix, module.getName());
                    desc = module.getCategory().toString();
                    descPosX = x + boxWidth;
                    descPosY = y;
                    hasDesc = true;
                } else {
                    drawTextBox(x, y, boxWidth, boxHeight, offTextColor, normalBgColor, modPrefix, module.getName());
                }
                u++;
            }
        }
    }
}
