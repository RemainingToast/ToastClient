package toast.client.events;

public abstract class AbstractSkippableEvent {

    private boolean isSkipped;

    protected AbstractSkippableEvent() {

    }

    public boolean isCancelled() {
        return this.isSkipped;
    }

    public void setCancelled(boolean isCancelled) {
        this.isSkipped = isCancelled;
    }

}
