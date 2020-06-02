package toast.client.modules.render;

import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import toast.client.event.EventImpl;
import toast.client.event.events.player.EventRender;
import toast.client.modules.Module;

import java.awt.*;

import static toast.client.utils.ShulkerBoxUtils.getItemsInShulker;
import static toast.client.utils.ShulkerBoxUtils.isShulkerBox;
import static toast.client.utils.TwoDRenderUtils.*;

public class ShulkerPreview extends Module {
    public ShulkerPreview() {
        super("ShulkerPreview", "Displays contents of a shulker box when hovered over in inventory", Category.RENDER, -1);
    }

    @Override
    public void onEnable() {
        disable();
    }

    @EventImpl
    public void onRender(EventRender event) {
        if (mc.player != null && mc.currentScreen instanceof ContainerScreen) {
            ContainerScreen containerScreen = (ContainerScreen) mc.currentScreen;
            Container container = containerScreen.getContainer();

            for (int i = 0; i < container.slots.size(); ++i) {
                Slot slot = container.slots.get(i);
                if (isShulkerBox(slot.getStack().getItem())) {
                    int sx = (slot.xPosition + (mc.getWindow().getWidth() / 2) - (mc.currentScreen.width / 2)) * 2 - 10;
                    int sy = slot.yPosition + (mc.getWindow().getWidth() / 2) - (mc.currentScreen.height / 2) - 100;
                    if (isMouseOverRect(mc.mouse.getX(), mc.mouse.getY(), sx, sy, 16, 16)) {
                        if (isShulkerBox(slot.getStack())) {
                            int x = (int) Math.round(mc.mouse.getX());
                            int y = (int) Math.round(mc.mouse.getY());
                            drawHollowRect(x, y, 153, 51, 1, new Color(68, 0, 175).getRGB());
                            drawRect(x, y, 153, 51, new Color(0, 0, 0, 255).getRGB());
                            renderNineWideInvItems(getItemsInShulker(slot.getStack()), x, y);
                        }
                    }
                }
            }
        }
    }
}
