package toast.client.gui.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import toast.client.ToastClient;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;
import toast.client.modules.misc.Panic;

import java.awt.*;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import static toast.client.utils.TwoDRenderUtils.drawRect;

public class HUD {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void drawHUD() {
        if (mc.options.debugEnabled || Panic.IsPanicking()) return;
        Module hud = ModuleManager.getModule("HUD");
        if (hud == null) return;
        if (!hud.isEnabled()) return;

        boolean rgb = false;
        if (hud.getBool("Rainbow")) {
            rgb = true;
        }

        // watermark
        if (hud.getBool("Watermark")) {
            double size = hud.getDouble("Watermark Size");
            GL11.glScaled(size, size, size);
            String[] letters = ToastClient.cleanPrefix.split(""); //TODO: fix the positions of the letters maybe not hardcoded in the future?
            int[] intarray = {7, 13, 19, 25, 31, 37, 43, 46, 48, 54, 60};
            for (int i = 0; i < letters.length; i++) {
                mc.textRenderer.drawWithShadow(letters[i], intarray[i], 4, rainbow(i * 25));
            }
            GL11.glScaled(1d / size, 1d / size, 1d / size);
        }

        // invenotry preview
        if (hud.getBool("Inventory")) {
            mc.getWindow().getScaledHeight();
            int startX = mc.getWindow().getScaledWidth() / 2 + 100;
            int startY = mc.getWindow().getScaledHeight() - 51;
            int x = startX;
            int y = startY;
            if (hud.getBool("Inventory BG")) drawRect(x, y, 153, 51, new Color(125, 125, 125, 175).getRGB());
            if (mc.player != null) {
                int i = 0;
                for (ItemStack itemStack : mc.player.inventory.main) {
                    if (i > 8) {
                        if (!itemStack.isEmpty()) mc.getItemRenderer().renderGuiItem(itemStack, x + 1, y + 1);
                        if (x == 17 * 8 + startX) {
                            x = startX;
                            y += 17;
                        } else x += 17;
                    }
                    i++;
                }
            }
        }

        // arraylist/sortedset/modulelist/whatever
        if (hud.getBool("SortedSet")) {
            SortedSet<String> enabledModules = new TreeSet<>(Comparator.comparing(mc.textRenderer::getStringWidth).reversed()); //TODO: fix so this also works if there are 2 modules with the same name length rn if there are it will just include 1 in the list
            for (Module module : ModuleManager.modules) {
                if (module.isEnabled() && module.getBool("Visible")) {
                    enabledModules.add(module.getName());
                }
            }
            int count = 0;
            for (String moduleName : enabledModules) {
                count++;
                int windowWidth = mc.getWindow().getScaledWidth();
                int moduleNameWidth = mc.textRenderer.getStringWidth(moduleName);
                int fontHeight = mc.textRenderer.fontHeight;

                int textcolor;
                int borderColor;
                if (rgb) {
                    textcolor = HTB((count) * 35);
                    borderColor = HTB(50);
                } else {
                    textcolor = new Color(255, 107, 66).getRGB();
                    borderColor = new Color(255, 50, 66).getRGB();
                }

                int extralineoffset = 0;

                if (hud.getBool("Right line")) {
                    extralineoffset = 1;
                }

                //background
                InGameHud.fill(
                        windowWidth - moduleNameWidth - 3 - extralineoffset,
                        (count * 12) - 12,
                        windowWidth - extralineoffset,
                        5 + fontHeight + (count * 12) - 14,
                        new Color(0, 0, 0, 90).getRGB());

                //modulename
                mc.textRenderer.draw(moduleName,
                        windowWidth - moduleNameWidth - 1 - extralineoffset,
                        (count * 12) - 10,
                        textcolor);

                if (hud.getBool("Left line")) {
                    // | <-- line
                    InGameHud.fill(
                            windowWidth - moduleNameWidth - 3 - extralineoffset,
                            (count * 12) - 12,
                            windowWidth - moduleNameWidth - 4 - extralineoffset,
                            5 + fontHeight + (count * 12) - 14,
                            borderColor);
                }

                if (hud.getBool("Middle line")) {
                    // __ \/ line
                    InGameHud.fill(
                            windowWidth - moduleNameWidth - 4,
                            5 + fontHeight + (count * 12) - 14,
                            windowWidth - extralineoffset,
                            5 + fontHeight + (count * 12) - 15,
                            borderColor);
                }

                if (extralineoffset >= 1) {
                    // --> | line
                    InGameHud.fill(
                            windowWidth,
                            0,
                            windowWidth - 1,
                            5 + fontHeight + (count * 12) - 14,
                            borderColor);
                }
            }
        }
    }

    public static int rainbow(final int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) >> 2);
        rainbowState %= 360.0;
        return Color.getHSBColor((float) (rainbowState / 360.0), 0.2f, 3f).getRGB();
    }

    public static int HTB(final int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) >> 2);
        rainbowState %= 360.0;
        return Color.getHSBColor((float) (rainbowState / 360.0), (float) (0.3),
                (float) (0.97)).getRGB();
    }
}
