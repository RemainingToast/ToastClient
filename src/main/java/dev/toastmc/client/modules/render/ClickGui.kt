package dev.toastmc.client.modules.render

import dev.toastmc.client.gui.MyMinecraftScreen
import dev.toastmc.client.modules.Module
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW

class ClickGui : Module("ClickGui", "The gui for managing modules.", Category.RENDER, GLFW.GLFW_KEY_RIGHT_SHIFT) {

    var clickGuiHasOpened: Boolean? = null

    override fun onEnable() {
        if (mc.player != null) {
            if(mc.world != null) {
                MinecraftClient.getInstance().openScreen(MyMinecraftScreen())
            }
        }
    }

    override fun onDisable() {
        clickGuiHasOpened = false
    }

}