package dev.toastmc.client.events.player;

import dev.toastmc.client.events.AbstractSkippableEvent;

public class EventRender extends AbstractSkippableEvent {
    private final int partialTicks;

    public EventRender(int partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}
