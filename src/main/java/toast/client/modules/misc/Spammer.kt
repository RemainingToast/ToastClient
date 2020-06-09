package toast.client.modules.misc

import com.google.common.eventbus.Subscribe
import org.apache.commons.lang3.RandomStringUtils
import toast.client.events.player.EventUpdate
import toast.client.modules.Module
import toast.client.utils.FileManager
import toast.client.utils.Logger
import toast.client.utils.TimerUtil
import java.util.*

class Spammer : Module("Spammer", "Spams messages in chat from a file.", Category.MISC, -1) {
    private var lines: List<String>? = ArrayList()
    private var currentLine = 0
    private val timer = TimerUtil()
    override fun onEnable() {
        timer.reset()
        lines = FileManager.readFile(FileManager.createFile("spammer.txt"))
        if (lines == null || lines!!.isEmpty()) {
            Logger.message("spammer.txt was empty!", Logger.WARN, false)
            this.disable()
        }
    }

    override fun onDisable() {
        timer.reset()
    }

    @Subscribe
    fun onTick(event: EventUpdate?) {
        if (lines!!.isEmpty() || mc.player == null) return
        if (timer.isDelayComplete((getDouble("Delay") * 1000L).toLong())) {
            timer.setLastMS()
            when (getBool("AntiSpam")) {
                false -> mc.player!!.sendChatMessage(lines!![currentLine])
                true -> mc.player!!.sendChatMessage(lines!![currentLine] + " [" + RandomStringUtils.randomAlphanumeric(getDouble("AntiSpam length").toInt()).toLowerCase() + "]")
            }
            if (currentLine >= lines!!.size - 1) currentLine = 0
            else currentLine++
        }
    }

    init {
        settings.addSlider("Delay", 0.0, 1.0, 20.0)
        settings.addBoolean("AntiSpam", false)
        settings.addSlider("AntiSpam length", 1.0, 20.0, 25.0)
    }
}