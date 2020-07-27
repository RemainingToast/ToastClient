package dev.toastmc.client.event

import dev.toastmc.client.ToastClient
import me.zero.alpine.event.type.Cancellable

/**
 * @author 086
 */
open class ToastEvent : Cancellable(){
    var era = Era.PRE
        protected set
    val partialTicks: Float = ToastClient.MINECRAFT.tickDelta

    enum class Era {
        PRE, POST
    }
}