package dev.toastmc.toastclient.api.events

import net.minecraft.network.packet.Packet
import org.quantumclient.energy.Event

open class PacketEvent(val packet: Packet<*>) : Event() {
    class Receive(packet: Packet<*>) : PacketEvent(packet)
    class Send(packet: Packet<*>) : PacketEvent(packet)
}