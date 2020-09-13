package dev.toastmc.client.util

import baritone.api.BaritoneAPI

object Baritone {

    val BARITONE = BaritoneAPI.getProvider().primaryBaritone

    private val present = try {
        println(BaritoneAPI.getProvider().primaryBaritone)
        true
    } catch (e: ClassNotFoundException) {
        false
    }

    operator fun invoke(function: () -> Unit) {
        if (present) function()
    }

    fun baritonePresent(): Boolean {
        return present
    }
}