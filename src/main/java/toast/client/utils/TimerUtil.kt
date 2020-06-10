package toast.client.utils

/**
 * Class contains methods to time things and get delays
 */
class TimerUtil {
    private var lastMS: Long = 0

    fun everyDelay(delay: Long): Boolean {
        if (System.currentTimeMillis() - lastMS >= delay) {
            lastMS = System.currentTimeMillis().coerceAtMost(lastMS + delay)
            return true
        }
        return false
    }

    fun hasReached(f: Float): Boolean {
        return (System.currentTimeMillis() - lastMS).toFloat() >= f
    }

    /**
     * Checks if and amount of time has passed since the last reset
     */
    fun isDelayComplete(delay: Double): Boolean {
        return System.currentTimeMillis() - lastMS >= delay
    }

    /**
     * Returns the amount of time in milliseconds that has passed since last resetting the timer
     */
    fun pastTime(): Int {
        return (System.currentTimeMillis() - lastMS).toInt()
    }

    /**
     * Sets the last recorded millisecond to the current time in milliseconds
     */
    fun reset() {
        lastMS = System.currentTimeMillis()
    }

    init {
        reset()
    }
}