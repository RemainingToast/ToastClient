package toast.client.utils

import com.google.common.eventbus.Subscribe
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import net.minecraft.util.math.MathHelper
import toast.client.ToastClient
import toast.client.ToastClient.MODULE_MANAGER
import toast.client.events.network.EventPacketReceived
import toast.client.events.network.EventSyncedUpdate
import toast.client.events.player.EventUpdate
import toast.client.modules.misc.TPSSync
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

/**
 * Contains methods to calculate and get the current TPS (Ticks Per Second)
 */
@Environment(EnvType.CLIENT)
class TPSCalculator {
    private var lastUpdateMillis: Long = -1
    private val delays = IntArray(5)
    private var avgDelay : Double = -1.0
    private var nextPredictedTick = Double.MAX_VALUE
    private var updatesLeft = 20
    private var nextToSet = 0
    private var df = DecimalFormat("0.00")

    /**
     * Method called when client receives a packet
     */
    @Subscribe
    fun onPacketReceived(event: EventPacketReceived) {
        if (event.packet is WorldTimeUpdateS2CPacket) {
            updatesLeft = 20
            if (lastUpdateMillis != -1L) {
                delays[nextToSet] = (System.currentTimeMillis() - lastUpdateMillis).toInt()
                if (nextToSet < 4) nextToSet++ else nextToSet = 0
                var avgDelay = 0.0
                for (i in 0..4) {
                    avgDelay += delays[i]
                }
                avgDelay /= 5
                tps = df.format(MathHelper.clamp(20000 / avgDelay, 0.0, 20.0)).toDouble()
                nextPredictedTick = System.currentTimeMillis() + 1000 / tps
            }
            lastUpdateMillis = System.currentTimeMillis()
        }
    }

    /**
     * Method called every game tick
     */
    @Subscribe
    fun onUpdate(event: EventUpdate?) {
        if (MinecraftClient.getInstance().world == null) {
            updatesLeft = 20
            lastUpdateMillis = -1L
            nextPredictedTick = 0.0
            tps = 20.0
            Arrays.fill(delays, 1000)
            return
        }
        if (tps == 20.0) {
            val event = EventSyncedUpdate()
            ToastClient.eventBus.post(event)
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
        var tps: Double = -1.0
        private var timer: Timer? = null
    }

    init {
        ToastClient.eventBus.register(this)
        df.roundingMode = RoundingMode.HALF_UP
        lastUpdateMillis = -1L
        tps = 20.0
        avgDelay = -1.0
        Arrays.fill(delays, 1000)
        if (timer == null) {
            timer = Timer()
            timer!!.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (tps != 20.0 && MODULE_MANAGER.getModule(TPSSync::class.java)!!.enabled && System.currentTimeMillis() >= nextPredictedTick && updatesLeft > 0) {
                        val event = EventSyncedUpdate()
                        ToastClient.eventBus.post(event)
                        updatesLeft--
                        Thread.sleep((1000 / tps).toLong())
                        if (updatesLeft == 0) {
                            avgDelay = 0.0
                        }
                    }
                }
            }, 0, 1)
        }
    }
}