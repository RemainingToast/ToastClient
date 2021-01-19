package me.remainingtoast.toastclient.api.gui

import com.lukflug.panelstudio.Context
import com.lukflug.panelstudio.settings.KeybindComponent
import com.lukflug.panelstudio.settings.KeybindSetting
import com.lukflug.panelstudio.theme.Renderer
import org.lwjgl.glfw.GLFW

class ToastKeybind(renderer: Renderer, keybind: KeybindSetting) : KeybindComponent(renderer, keybind) {
    override fun handleKey(context: Context, scancode: Int) {
        context.setHeight(renderer.getHeight(false))
        if (hasFocus(context) && scancode == GLFW.GLFW_KEY_DELETE) {
            keybind.key = 0
            releaseFocus()
            return
        }
        super.handleKey(context, scancode)
    }
}