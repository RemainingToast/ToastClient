package toast.client.utils

import com.google.common.eventbus.Subscribe
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import net.minecraft.util.math.MathHelper
import toast.client.ToastClient
import toast.client.events.network.EventPacketReceived
import toast.client.events.network.EventSyncedUpdate
import toast.client.events.player.EventUpdate
import java.util.*

/**
 * Contains methods to calculate and get the current TPS (Ticks Per Second)
 */
@Environment(EnvType.CLIENT)
class TPSCalculator {
    private var lastUpdateMillis: Long = 0
    private val ticks = FloatArray(10)
    private var nextPredictedTick = Double.MAX_VALUE
    private var nextToCheck = 0

    /**
     * Method called when client receives a packet
     */
    @Subscribe
    fun onPacketReceived(event: EventPacketReceived) {
        if (event.getPacket() is WorldTimeUpdateS2CPacket) {
            ticks[nextToCheck] = MathHelper.clamp(
                    1000.0 / (System.currentTimeMillis() - lastUpdateMillis),
                    0.0,
                    20.0
            ).toFloat()
            lastUpdateMillis = System.currentTimeMillis()
            var totalTicks = 0.0
            var ticksStored = 0
            for (i in ticks) {
                if (i > 0) {
                    totalTicks += i.toDouble()
                    ticksStored++
                }
            }
            tps = MathHelper.clamp(totalTicks / ticksStored, 0.0, 20.0)
            nextPredictedTick = System.currentTimeMillis() + 1000 / tps
        }
        if (nextToCheck > ticks.size - 1) nextToCheck = 0
    }

    /**
     * Method called every game tick
     */
    @Subscribe
    fun onUpdate(event: EventUpdate?) {
        if (MinecraftClient.getInstance().world == null) {
            lastUpdateMillis = 0
            tps = 20.0
            Arrays.fill(ticks, 0f)
        }
    }

    companion object {
        /**
         * Current instance of the TPS calculator
         */
        @JvmField
        var calculatorInstance: TPSCalculator? = null

        /**
         * Current TPS
         */
        var tps: Double = 0.0
        private var timer: Timer? = null
    }

    init {
        ToastClient.eventBus.register(this)
        lastUpdateMillis = 0
        tps = 20.0
        Arrays.fill(ticks, 0f)
        if (timer == null) {
            timer = Timer()
            timer!!.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (System.currentTimeMillis() >= nextPredictedTick) {
                        val event = EventSyncedUpdate()
                        ToastClient.eventBus.post(event)
                    }
                }
            }, 0, 1)
        }
    }
}