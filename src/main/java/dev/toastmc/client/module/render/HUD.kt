package dev.toastmc.client.module.render

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.OverlayEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.TwoDRenderUtils
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import java.awt.Color
import java.util.*
import kotlin.math.ceil

@ModuleManifest(
    label = "HUD",
    category = Category.RENDER
)
class HUD : Module() {
    @Setting(name = "Arraylist") var arraylist = true
    @Setting(name = "Watermark") var watermark = true

    var infoList: List<String> = ArrayList()

    @EventHandler
    private val onOverlayEvent = Listener(EventHook<OverlayEvent> {
        var arrayCount: Int = 0
        if (watermark && arraylist && !mc.options.debugEnabled) {
            val lines: MutableList<String> = ArrayList()
            lines.clear()
            if (watermark) lines.add(0, "Toast Client " + ToastClient.MODVER)
            if (arraylist) {
                for (m in ToastClient.MODULE_MANAGER.modules) if (m.enabled) lines.add(m.label)
                lines.sortWith(Comparator { a: String?, b: String? ->
                    mc.textRenderer.getWidth(b).compareTo(mc.textRenderer.getWidth(a))
                })
                val color: Int = getRainbow(1f, 1f, 10.0, 0)
                for (s in lines) {
                    TwoDRenderUtils.drawText(it.matrix, s, 2, 1 + (arrayCount * 10), color)
                    arrayCount++
                }
            }
        }
    })

    override fun onEnable() {
        super.onEnable()
        ToastClient.EVENT_BUS.subscribe(onOverlayEvent)
    }

    override fun onDisable() {
        super.onDisable()
        ToastClient.EVENT_BUS.unsubscribe(onOverlayEvent)
    }

    private fun getRainbow(sat: Float, bri: Float, speed: Double, offset: Int): Int {
        var rainbowState = ceil((System.currentTimeMillis() + offset) / speed)
        rainbowState %= 360.0
        return Color.HSBtoRGB((rainbowState / 360.0).toFloat(), sat, bri)
    }
}