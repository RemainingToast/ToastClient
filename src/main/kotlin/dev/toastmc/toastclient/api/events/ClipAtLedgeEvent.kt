package dev.toastmc.toastclient.api.events

import net.minecraft.entity.player.PlayerEntity
import org.quantumclient.energy.Event

class ClipAtLedgeEvent(val player: PlayerEntity, var clip: Boolean = false) : Event()