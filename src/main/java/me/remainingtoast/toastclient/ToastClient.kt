package me.remainingtoast.toastclient

import me.remainingtoast.toastclient.api.event.KeyPressEvent
import me.remainingtoast.toastclient.api.module.ModuleManager
import me.remainingtoast.toastclient.api.setting.SettingManager
import me.remainingtoast.toastclient.api.util.KeyUtil
import me.remainingtoast.toastclient.client.ToastGUI
import me.remainingtoast.toastclient.client.module.ClickGUIModule
import me.zero.alpine.bus.EventBus
import me.zero.alpine.bus.EventManager
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW


class ToastClient : ModInitializer {

    companion object {
        val MODNAME = "Toast Client"
        val MODVER = "1.2.0"
        val SETTING_MANAGER = SettingManager()
        val MODULE_MANAGER = ModuleManager()
        val CLICKGUI = ToastGUI()
        val mc = MinecraftClient.getInstance()

        var CMD_PREFIX = "."

        @JvmField
        val EVENT_BUS: EventBus = EventManager()
    }

    override fun onInitialize() {

        val clickGuiKeyBind: KeyBinding = KeyBindingHelper.registerKeyBinding(KeyBinding("key.toastclient.gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.toastclient.gui"))
        ClientTickEvents.END_CLIENT_TICK.register {
            if (clickGuiKeyBind.wasPressed() && mc.world != null && mc.player != null) MODULE_MANAGER.toggleModule(ClickGUIModule())
        }

        Runtime.getRuntime().addShutdownHook(Thread {
            println("${MODNAME.toUpperCase()} SAVING AND SHUTTING DOWN")
        })
    }
}