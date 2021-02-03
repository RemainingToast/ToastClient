package me.remainingtoast.toastclient.client.module.client

import me.remainingtoast.toastclient.ToastClient
import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.setting.Setting.IntegerSetting
import org.lwjgl.glfw.GLFW

object ClickGUIModule : Module("ClickGUI", Category.CLIENT, GLFW.GLFW_KEY_RIGHT_SHIFT) {

    var animationSpeed: IntegerSetting = registerInteger("Animation Speed", "The speed for GUI animations.", 200, 0, 1000, true)
    var scrollSpeed: IntegerSetting = registerInteger("Scroll Speed", "The speed for GUI scrolling", 10, 1, 20, true)

    override fun onEnable() {
        ToastClient.CLICKGUI.enterGUI()
        disable()
    }
}
