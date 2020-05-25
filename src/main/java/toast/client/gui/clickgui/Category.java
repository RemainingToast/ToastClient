package toast.client.gui.clickgui;

import toast.client.dontobfuscate.ClickGuiSettings;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;

import static toast.client.gui.clickgui.ClickGuiScreen.*;

public class Category {
    public boolean hasDesc = false;
    public String category;
    public int descPosX = 0;
    public int descPosY = 0;
    public String desc = "";
    public static ClickGuiSettings.Colors colors = settings.getColors();
    public Category(int mouseX, int mouseY, int boxWidth, int boxHeight, Module.Category category, boolean clicked) {
        this.category = category.toString();
        int x = settings.getPositions(category.toString()).getPosX();
        int catY = settings.getPositions(category.toString()).getPosY();
        int catBgColor = colors.categoryBgColor;
        if (isMouseOverRect(mouseX, mouseY, x, catY, boxWidth, boxHeight)) {
            if (clicked) {
                catBgColor = colors.categoryClickColor;
                settings.getPositions(this.category).setExpanded(!settings.getPositions(this.category).isExpanded());
            } else {
                catBgColor = colors.categoryHoverBgColor;
            }
        }
        drawTextBox(x, catY, boxWidth, boxHeight, colors.categoryBoxColor, colors.categoryTextColor, colors.categoryPrefixColor, catBgColor, colors.categoryPrefix, category.toString());
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
                    if (clicked) {
                        moduleBgColor = colors.moduleClickColor;
                        module.toggle();
                    } else {
                        moduleBgColor = colors.moduleHoverBgColor;
                    }
                    desc = module.getDescription();
                    descPosX = x + boxWidth;
                    descPosY = y;
                    hasDesc = true;
                }
                drawTextBox(x, y, boxWidth, boxHeight, colors.moduleBoxColor, moduleTextColor, colors.modulePrefixColor, moduleBgColor, colors.modulePrefix, module.getName());
                u++;
            }
        }
    }
}
