package dev.toastmc.client.module.render

import baritone.api.BaritoneAPI
import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.OverlayEvent
import dev.toastmc.client.event.PacketEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.FabricReflect
import dev.toastmc.client.util.TwoDRenderUtils
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*
import kotlin.math.ceil
import kotlin.math.roundToInt


@ModuleManifest(
    label = "HUD",
    category = Category.RENDER
)
class HUD : Module() {
    @Setting(name = "Arraylist") var arraylist = true
    @Setting(name = "Watermark") var watermark = true
    @Setting(name = "Coords") var coords = true
    @Setting(name = "TPS") var tps = false
    @Setting(name = "FPS") var fps = false
    @Setting(name = "Ping") var ping = false
    @Setting(name = "Goal") var goal = true
    @Setting(name = "LagNotifier") var lagNotifier = true
    @Setting(name = "Armour") var armour = true

    var infoList: MutableList<String> = ArrayList()
    var lines: MutableList<String> = ArrayList()
    var lastPacket = 0L
    var tpsNum = 20.0
    var prevTime = 0

    @EventHandler
    private val onOverlayEvent = Listener(EventHook<OverlayEvent> {
        infoList.clear()
        lines.clear()
        var arrayCount = 0


        if (watermark && !mc.options.debugEnabled) lines.add(0, "Toast Client " + ToastClient.MODVER)
        if (arraylist && !mc.options.debugEnabled) {
            for (m in ToastClient.MODULE_MANAGER.modules) if (m.enabled && !m.hidden) lines.add(m.label)
            lines.sortWith(Comparator { a: String?, b: String? ->
                mc.textRenderer.getWidth(b).compareTo(mc.textRenderer.getWidth(a))
            })
            val color: Int = getRainbow(1f, 1f, 10.0, 0)
            for (s in lines) {
                TwoDRenderUtils.drawText(it.matrix, s, 5, 5 + (arrayCount * 10), color)
                arrayCount++
            }
        }
        if (coords) {
            val direction = when (mc.player!!.horizontalFacing) {
                Direction.NORTH -> "-Z"
                Direction.SOUTH -> "+Z"
                Direction.WEST -> "-X"
                Direction.EAST -> "+X"
                else -> ""
            }
            val direction2 = mc.player!!.horizontalFacing.toString().capitalize()
            val nether = mc.world!!.registryKey.value.path.contains("nether")
            val pos = mc.player!!.blockPos
            val vec: Vec3d = mc.player!!.pos
            val pos2: BlockPos = if (nether) BlockPos(vec.getX() * 8, vec.getY(), vec.getZ() * 8) else BlockPos(vec.getX() / 8, vec.getY(), vec.getZ() / 8)

            infoList.add(" [ $direction | $direction2 ] " + (if (nether) "\u00a7c" else "\u00a7a") + pos.x + " " + pos.y + " " + pos.z + " \u00a77[" + (if (nether) "\u00a7a" else "\u00a7c") + pos2.x + " " + pos2.y + " " + pos2.z + "\u00a77]")
        }
        if(goal) {
            infoList.add("Goal: ${if(BaritoneAPI.getProvider().primaryBaritone.customGoalProcess.isActive) BaritoneAPI.getProvider().primaryBaritone.customGoalProcess.goal.toString() else "none"}")

        }
        if (tps) {
            var suffix = "\u00a77"
            if (lastPacket + 7500 < System.currentTimeMillis()) suffix += "...." else if (lastPacket + 5000 < System.currentTimeMillis()) suffix += "..." else if (lastPacket + 2500 < System.currentTimeMillis()) suffix += ".." else if (lastPacket + 1200 < System.currentTimeMillis()) suffix += "."
            infoList.add("TPS: " + getColorString(tpsNum.toInt(), 18, 15, 12, 8, 4, false) + tpsNum.toInt() + suffix)
        }
        if (fps) {
            val fps = FabricReflect.getFieldValue(MinecraftClient.getInstance(), "field_1738", "currentFps") as Int
            infoList.add("FPS: " + getColorString(fps, 120, 60, 30, 15, 10, false) + fps)
        }
        if (ping) {
            val playerEntry = mc.player!!.networkHandler.getPlayerListEntry(mc.player!!.gameProfile.id)
            val ping = playerEntry?.latency ?: 0
            infoList.add("Ping: " + getColorString(ping, 10, 50, 100, 300, 600, true) + ping)
        }
        if (lagNotifier) {
            val time = System.currentTimeMillis()
            if (time - lastPacket > 500) {
                val text = "The server has been lagging for " + (time - lastPacket) / 1000.0 + "s"
                TwoDRenderUtils.drawText(
                        it.matrix, text, mc.window.scaledWidth / 2 - mc.textRenderer.getWidth(text) / 2, Math.min(
                        (time - lastPacket - 500) / 20 - 20,
                        10
                ).toInt(), 0xd0d0d0
                )
            }
        }
        if (armour && !mc.player?.isCreative!! && !mc.player?.isSpectator!!) {
            GL11.glPushMatrix()
            var count = 0
            val x1 = mc.window.scaledWidth / 2
            val y =
                    mc.window.scaledHeight - if (mc.player!!.isSubmergedInWater || mc.player!!.air < mc.player!!.maxAir) 64 else 55
            for (`is` in mc.player!!.inventory.armor) {
                count++
                if (`is`.isEmpty) continue
                val x = x1 - 90 + (9 - count) * 20 + 2
                GL11.glEnable(GL11.GL_DEPTH_TEST)
                mc.itemRenderer.zOffset = 200f
                mc.itemRenderer.renderGuiItemIcon(`is`, x, y)

                mc.itemRenderer.renderGuiItemOverlay(mc.textRenderer, `is`, x, y)

                mc.itemRenderer.zOffset = 0f
                GL11.glDisable(GL11.GL_DEPTH_TEST)
            }
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glPopMatrix()
        }
        for ((i, s) in infoList.withIndex()) {
            TwoDRenderUtils.drawText(it.matrix, s, 10, mc.window.scaledHeight - 20 - (i * 10), 0xa0a0a0)
        }
    })

    @EventHandler
    private val packetEventListener = Listener(EventHook<PacketEvent.Receive> {
        lastPacket = System.currentTimeMillis()
        if (it.packet is WorldTimeUpdateS2CPacket) {
            val time = System.currentTimeMillis()
            if (time < 500) return@EventHook
            val timeOffset: Long = Math.abs(1000 - (time - prevTime)) + 1000
            tpsNum = (MathHelper.clamp(20 / (timeOffset.toDouble() / 1000), 0.0, 20.0) * 100.0).roundToInt() / 100.0
            prevTime = time.toInt()
        }
    })

    override fun onEnable() {
        super.onEnable()
        ToastClient.EVENT_BUS.subscribe(onOverlayEvent)
        ToastClient.EVENT_BUS.subscribe(packetEventListener)
    }

    override fun onDisable() {
        super.onDisable()
        ToastClient.EVENT_BUS.unsubscribe(onOverlayEvent)
        ToastClient.EVENT_BUS.unsubscribe(packetEventListener)
    }

    private fun getColorString(value: Int, best: Int, good: Int, mid: Int, bad: Int, worst: Int, rev: Boolean): String? {
        return if (if (!rev) value > best else value < best) "\u00a72" else if (if (!rev) value > good else value < good) "\u00a7a" else if (if (!rev) value > mid else value < mid) "\u00a7e" else if (if (!rev) value > bad else value < bad) "\u00a76" else if (if (!rev) value > worst else value < worst) "\u00a7c" else "\u00a74"
    }

    private fun getRainbow(sat: Float, bri: Float, speed: Double, offset: Int): Int {
        var rainbowState = ceil((System.currentTimeMillis() + offset) / speed)
        rainbowState %= 360.0
        return Color.HSBtoRGB((rainbowState / 360.0).toFloat(), sat, bri)
    }
}