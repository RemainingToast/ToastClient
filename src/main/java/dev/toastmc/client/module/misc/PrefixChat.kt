package dev.toastmc.client.module.misc

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.KeyPressEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.KeyUtil
import dev.toastmc.client.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.client.gui.screen.ChatScreen

@ModuleManifest(
        label = "PrefixChat",
        description = "Opens chat with prefix inside when prefix key pressed.",
        category = Category.MISC
)
class PrefixChat : Module() {

    override fun onEnable() {
        super.onEnable()
        ToastClient.EVENT_BUS.subscribe(onKeyPressEvent)
    }

    override fun onDisable() {
        super.onDisable()
        ToastClient.EVENT_BUS.unsubscribe(onKeyPressEvent)
    }

    @EventHandler
    private val onKeyPressEvent =  Listener(EventHook<KeyPressEvent> {
        if (mc.player == null || ToastClient.CMD_PREFIX.length != 1) return@EventHook
        if (it.key == KeyUtil.getKeyCode(ToastClient.CMD_PREFIX) && mc.currentScreen == null) mc.openScreen(ChatScreen(""))
    })
}