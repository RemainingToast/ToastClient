package toast.client.gui.hud.clickgui;

import toast.client.dontobfuscate.ClickGuiSettings;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;

import static toast.client.gui.hud.clickgui.ClickGuiScreen.*;

public class Category {
    public boolean hasDesc = false;
    public int descPosX = 0;
    public int descPosY = 0;
    public String desc = "";
    public static ClickGuiSettings.Colors colors = settings.getColors();
    public Category(int mouseX, int mouseY, int boxWidth, int boxHeight, Module.Category category) {
        int x = settings.getPositions(category.toString()).getPosX();
        int catY = settings.getPositions(category.toString()).getPosY();
        if (isMouseOverRect(mouseX, mouseY, x, catY, boxWidth, boxHeight)) {
            drawTextBox(x, catY, boxWidth, boxHeight, colors.categoryBoxColor, colors.categoryTextColor, colors.categoryPrefixColor, colors.categoryHoverBgColor, colors.categoryPrefix, category.toString());
        } else {
            drawTextBox(x, catY, boxWidth, boxHeight, colors.categoryBoxColor, colors.categoryTextColor, colors.categoryPrefixColor, colors.categoryBgColor, colors.categoryPrefix, category.toString());
        }
        if (settings.getPositions(category.toString()).isExpanded()) {
            int u = 1;
            for (Module module : ModuleManager.getModulesInCategory(category)) {
                int y = catY + u + boxHeight * u;
                int moduleTextColor;
                int moduleBgColor;
                if (module.isEnabled()) {
                    moduleTextColor = colors.moduleOnTextColor;
                    moduleBgColor = colors.moduleOnBgColor;
                } else {
                    moduleTextColor = colors.moduleOffTextColor;
                    moduleBgColor = colors.moduleOffBgColor;
                }
                if (isMouseOverRect(mouseX, mouseY, x, y, boxWidth, boxHeight)) {
                    drawTextBox(x, y, boxWidth, boxHeight, colors.moduleBoxColor, moduleTextColor, colors.modulePrefixColor, colors.categoryHoverBgColor, colors.modulePrefix, module.getName());
                    desc = module.getCategory().toString();
                    descPosX = x + boxWidth;
                    descPosY = y;
                    hasDesc = true;
                } else {
                    drawTextBox(x, y, boxWidth, boxHeight, colors.moduleBoxColor, moduleTextColor, colors.modulePrefixColor, moduleBgColor, colors.modulePrefix, module.getName());
                }
                u++;
            }
        }
    }
}
