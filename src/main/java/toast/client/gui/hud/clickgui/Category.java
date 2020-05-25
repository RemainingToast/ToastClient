package toast.client.gui.hud.clickgui;

import toast.client.dontobfuscate.CategorySetting;
import toast.client.dontobfuscate.ClickGuiSettings;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;

import static toast.client.gui.hud.clickgui.ClickGuiScreen.drawTextBox;
import static toast.client.gui.hud.clickgui.ClickGuiScreen.isMouseOverRect;

public class Category {
    public boolean hasDesc = false;
    public int descPosX = 0;
    public int descPosY = 0;
    public String desc = "";
    public Category(int mouseX, int mouseY, int boxWidth, int boxHeight, Module.Category category) {
        CategorySetting settings = ClickGuiSettings.getPositions(category.toString());
        int x = settings.getPosX();
        int catY = settings.getPosY();
        if (isMouseOverRect(mouseX, mouseY, x, catY, boxWidth, boxHeight)) {
            drawTextBox(x, catY, boxWidth, boxHeight, ClickGuiSettings.colors.categoryBoxColor, ClickGuiSettings.colors.categoryTextColor, ClickGuiSettings.colors.categoryPrefixColor, ClickGuiSettings.colors.categoryBgColor, ClickGuiSettings.colors.categoryPrefix, category.toString());
        } else {
            drawTextBox(x, catY, boxWidth, boxHeight, ClickGuiSettings.colors.categoryBoxColor, ClickGuiSettings.colors.categoryTextColor, ClickGuiSettings.colors.categoryPrefixColor, ClickGuiSettings.colors.categoryHoverBgColor, ClickGuiSettings.colors.categoryPrefix, category.toString());
        }
        if (settings.isExpanded()) {
            int u = 1;
            for (Module module : ModuleManager.getModulesInCategory(category)) {
                int y = catY + u + boxHeight * u;
                int moduleTextColor;
                int moduleBgColor;
                if (module.isEnabled()) {
                    moduleTextColor = ClickGuiSettings.colors.moduleOnTextColor;
                    moduleBgColor = ClickGuiSettings.colors.moduleOnBgColor;
                } else {
                    moduleTextColor = ClickGuiSettings.colors.moduleOffTextColor;
                    moduleBgColor = ClickGuiSettings.colors.moduleOffBgColor;
                }
                if (isMouseOverRect(mouseX, mouseY, x, y, boxWidth, boxHeight)) {
                    drawTextBox(x, y, boxWidth, boxHeight, ClickGuiSettings.colors.moduleBoxColor, moduleTextColor, ClickGuiSettings.colors.modulePrefixColor, ClickGuiSettings.colors.categoryHoverBgColor, ClickGuiSettings.colors.modulePrefix, module.getName());
                    desc = module.getCategory().toString();
                    descPosX = x + boxWidth;
                    descPosY = y;
                    hasDesc = true;
                } else {
                    drawTextBox(x, y, boxWidth, boxHeight, ClickGuiSettings.colors.moduleBoxColor, moduleTextColor, ClickGuiSettings.colors.modulePrefixColor, moduleBgColor, ClickGuiSettings.colors.modulePrefix, module.getName());
                }
                u++;
            }
        }
    }
}
