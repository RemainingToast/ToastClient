package toast.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toast.client.modules.render.ShulkerPreview;
import toast.client.utils.Logger;

import java.awt.*;

import static toast.client.ToastClient.MODULE_MANAGER;
import static toast.client.utils.ShulkerBoxUtils.getItemsInShulker;
import static toast.client.utils.ShulkerBoxUtils.isShulkerBox;
import static toast.client.utils.TwoDRenderUtils.*;
import static toast.client.utils.TwoDRenderUtils.renderNineWideInvItems;

@Mixin(ContainerScreen.class)
public class MixinContainerScreen {
    private MinecraftClient mc = MinecraftClient.getInstance();
    @Inject(method = "render", at = @At("RETURN"))
    public void onRender(CallbackInfo ci) {
        if (mc.player != null && mc.currentScreen instanceof ContainerScreen && MODULE_MANAGER.getModule(ShulkerPreview.class).isEnabled()) {
            ContainerScreen containerScreen = (ContainerScreen) mc.currentScreen;
            Container container = containerScreen.getContainer();

            for (int i = 0; i < container.slots.size(); ++i) {
                Slot slot = container.slots.get(i);
                if (isShulkerBox(slot.getStack().getItem())) {
                    int sx = (slot.xPosition + (mc.getWindow().getWidth() / 2) - (mc.currentScreen.width / 2)) * 2 - 10;
                    int sy = slot.yPosition + (mc.getWindow().getWidth() / 2) - (mc.currentScreen.height / 2) - 100;
                    if (isMouseOverRect(mc.mouse.getX(), mc.mouse.getY(), sx, sy, 16, 16)) {
                        if (isShulkerBox(slot.getStack())) {
                            Logger.message("Over a shulker", Logger.INFO, true);
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
