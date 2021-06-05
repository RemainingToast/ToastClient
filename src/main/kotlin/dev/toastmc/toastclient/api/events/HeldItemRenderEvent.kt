package dev.toastmc.toastclient.api.events

import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import org.quantumclient.energy.Event

class HeldItemRenderEvent(val matrixStack: MatrixStack, val hand: Hand, val item: ItemStack) : Event() {
}