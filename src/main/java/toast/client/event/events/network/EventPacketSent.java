package toast.client.event.events.network;

import toast.client.event.events.AbstractSkippableEvent;
import net.minecraft.network.Packet;

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
