package me.remainingtoast.toastclient.api.event

import me.remainingtoast.toastclient.api.util.mc
import me.zero.alpine.event.type.Cancellable

open class ToastEvent : Cancellable(){
    var era = Era.PRE
        protected set
    val partialTicks: Float = mc.tickDelta

    enum class Era {
        PRE, POST
    }
}