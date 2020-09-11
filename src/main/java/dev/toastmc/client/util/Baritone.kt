package dev.toastmc.client.util

import baritone.api.BaritoneAPI

// Credit: https://github.com/zeroeightysix/KAMI/pull/228/commits/febd45f8f65f624091f721504e378461ff26893d#diff-7350c975fecf0f5ad90d94c5b49fb33c
object Baritone {

    val BARITONE = BaritoneAPI.getProvider().primaryBaritone

    private val present = try {
        Class.forName("baritone.Baritone")
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