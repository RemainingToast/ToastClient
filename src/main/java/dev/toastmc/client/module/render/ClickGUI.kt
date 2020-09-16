package dev.toastmc.client.module.render

import dev.toastmc.client.ToastClient
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
//    var settings: ClickGuiSettings = ClickGuiSettings(guiScreen)

    @Setting(name = "Descriptions")
    var descriptions = true

    override fun onEnable() {
        if (mc.player != null) {
            if (mc.currentScreen == null) {
                guiScreen = ClickGuiScreen(this)
                mc.openScreen(guiScreen)
                ToastClient.MODULE_MANAGER.clickguiHasOpened = true
            }
        }
    }

    override fun onDisable() {
        if (mc.currentScreen is ClickGuiScreen && mc.player != null) {
            mc.openScreen(null)
            ToastClient.MODULE_MANAGER.clickguiHasOpened = false
        }
    }
}