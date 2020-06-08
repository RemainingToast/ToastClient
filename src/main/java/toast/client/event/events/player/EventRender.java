package toast.client.event.events.player;

import toast.client.event.events.AbstractSkippableEvent;

public class EventRender extends AbstractSkippableEvent {
    private final int partialTicks;

    public EventRender(int partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}
