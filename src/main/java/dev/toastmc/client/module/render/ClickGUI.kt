package dev.toastmc.client.module.render

import dev.toastmc.client.gui.click.ClickGuiScreen
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.mc

@ModuleManifest(
    label = "ClickGUI",
    category = Category.RENDER,
    key = 344
)
class ClickGUI : Module() {

    private var guiScreen: ClickGuiScreen? = null
    private var clickGuiHasOpened = false

    override fun onEnable() {
        if (mc.player != null) {
            if (guiScreen == null){
                guiScreen = ClickGuiScreen()
            }
            if (mc.currentScreen == null) {
                mc.openScreen(guiScreen)
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