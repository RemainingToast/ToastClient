package toast.client.utils;

public class TimerUtil {
    private long lastMS;

    public TimerUtil() { setLastMS(); }

    public int convertToMS(int perSecond) { return 1000 / perSecond; }

    public boolean everyDelay(long delay) {
        if (System.currentTimeMillis() - this.lastMS >= delay) {
            this.lastMS = Math.min(System.currentTimeMillis(), this.lastMS + delay);
            return true;
        }
        return false;
    }

    public long getCurrentMS() { return System.nanoTime() / 1000000L; }

    public long getLastMS() { return this.lastMS; }

    public boolean hasReached(float f) { return ((float)(getCurrentMS() - this.lastMS) >= f); }

    public boolean isDelayComplete(long delay) {
        if (System.currentTimeMillis() - this.lastMS >= delay)
            return true;
        return false;
    }

    public int pastTime() { return (int)(System.currentTimeMillis() - this.lastMS); }

    public void reset() { this.lastMS = getCurrentMS(); }

    public void setLastMS() { this.lastMS = System.currentTimeMillis(); }

    public void setLastMS(long currentMS) { this.lastMS = currentMS; }
}
