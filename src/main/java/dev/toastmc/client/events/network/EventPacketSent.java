package dev.toastmc.client.events.network;

import net.minecraft.network.Packet;
import dev.toastmc.client.events.AbstractSkippableEvent;

public class EventPacketSent extends AbstractSkippableEvent {
    public Packet packet;

    public EventPacketSent(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
