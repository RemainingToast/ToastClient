package dev.toastmc.client.module.misc

import dev.toastmc.client.ToastClient.Companion.EVENT_BUS
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.Discord
import dev.toastmc.client.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.arikia.dev.drpc.DiscordRPC

@ModuleManifest(
    label = "Discord",
    description = "Show Toast Client in Discord RPC",
    category = Category.MISC
)
class Discord : Module() {
    private var delay = 0
    private var ready = Discord.ready

    override fun onEnable() {
        delay = 0
        Discord.start()
        EVENT_BUS.subscribe(onTickEvent)
    }

    override fun onDisable() {
        Discord.end()
        delay = 0
        EVENT_BUS.unsubscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null || !ready) return@EventHook
        if (ready){
            delay++
            if (delay >= 200) {
                Discord.update()
                delay = 0
            }
        }
        DiscordRPC.discordRunCallbacks()
    })
}