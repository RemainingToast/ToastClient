package me.remainingtoast.toastclient.api.util

class TimerUtil {

    init {
        setLastMS()
    }

    var lastMS: Long = 0

    fun convertToMS(perSecond: Int): Int {
        return 1000 / perSecond
    }

    fun everyDelay(delay: Long): Boolean {
        if (System.currentTimeMillis() - lastMS >= delay) {
            lastMS = System.currentTimeMillis().coerceAtMost(lastMS + delay)
            return true
        }
        return false
    }

    val currentMS: Long
        get() = System.nanoTime() / 1000000L

    fun hasReached(f: Float): Boolean {
        return (currentMS - lastMS).toFloat() >= f
    }

    fun isDelayComplete(delay: Long): Boolean {
        return System.currentTimeMillis() - lastMS >= delay
    }

    fun pastTime(): Int {
        return (System.currentTimeMillis() - lastMS).toInt()
    }

    fun reset() {
        lastMS = currentMS
    }

    fun setLastMS() {
        lastMS = System.currentTimeMillis()
    }
}
