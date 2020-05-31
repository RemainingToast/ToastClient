package toast.client.event.events.network;

import toast.client.event.events.AbstractSkippableEvent;
import net.minecraft.network.Packet;

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
