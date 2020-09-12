package dev.toastmc.client.module.render

import dev.toastmc.client.gui.click.ClickGuiScreen
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.mc
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting

@ModuleManifest(
    label = "ClickGUI",
    category = Category.RENDER,
    key = 344
)
class ClickGUI : Module() {

    private var guiScreen: ClickGuiScreen? = null
    private var clickGuiHasOpened = false

    @Setting(name = "Descriptions")
    var descriptions = true

    override fun onEnable() {
        if (guiScreen == null) guiScreen = ClickGuiScreen(this)
        if (mc.player != null) {
            if (mc.currentScreen == null) {
                mc.openScreen(guiScreen)
                println("opened clickgui: ${mc.currentScreen is ClickGuiScreen}")
                clickGuiHasOpened = true
            }
        }
    }

    override fun onDisable() {
        if (mc.currentScreen is ClickGuiScreen && mc.player != null) {
            mc.openScreen(null)
            clickGuiHasOpened = false
        }
    }

}