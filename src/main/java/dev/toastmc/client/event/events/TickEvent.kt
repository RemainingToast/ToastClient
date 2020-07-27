package dev.toastmc.client.event.events

import dev.toastmc.client.event.ToastEvent

/**
 * @author 086
 */
open class TickEvent private constructor(private val stage: Stage) : ToastEvent(){
    enum class Stage {
        CLIENT
    }

    open class Client : TickEvent(Stage.CLIENT) {
        /**
         * This exists because many listeners for TickEvents will perform player null checks.
         * This event is ensured to only fire when the player and world is not null.
         */
        class InGame : Client()

        /**
         * @see InGame
         */
        class OutOfGame : Client()
    }
}