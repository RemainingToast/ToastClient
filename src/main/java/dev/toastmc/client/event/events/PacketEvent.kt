package dev.toastmc.client.event.events

import dev.toastmc.client.event.ToastEvent
import net.minecraft.network.Packet

open class PacketEvent(val packet: Packet<*>) : ToastEvent() {
    class Receive(packet: Packet<*>) : PacketEvent(packet)
    class Send(packet: Packet<*>) : PacketEvent(packet)
}