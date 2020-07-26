package toast.client.events.network;

import net.minecraft.network.Packet;
import toast.client.events.AbstractSkippableEvent;

public class EventPacketReceived extends AbstractSkippableEvent {
    public Packet packet;

    public EventPacketReceived(Packet packet) {
        this.packet = packet;
    }

    public EventPacketReceived() {

    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

}
