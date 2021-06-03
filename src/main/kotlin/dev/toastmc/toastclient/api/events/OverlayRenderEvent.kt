package dev.toastmc.toastclient.api.events

import net.minecraft.client.util.math.MatrixStack
import org.quantumclient.energy.Event

class OverlayRenderEvent(val matrix: MatrixStack) : Event()