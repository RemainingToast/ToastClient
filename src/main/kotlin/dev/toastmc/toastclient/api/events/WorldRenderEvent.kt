package dev.toastmc.toastclient.api.events

import net.minecraft.client.util.math.MatrixStack
import org.quantumclient.energy.Event

class WorldRenderEvent(val tickDelta: Float, val limitTime: Long, val matrixStack: MatrixStack) : Event()