package dev.toastmc.toastclient.api.events

import org.quantumclient.energy.Event

open class TickEvent private constructor(private val stage: Stage) : Event() {
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