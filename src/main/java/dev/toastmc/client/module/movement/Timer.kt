package dev.toastmc.client.module.movement

import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting

@ModuleManifest(
    label = "Timer",
    description = "Speed up client speed",
    category = Category.MOVEMENT
)
class Timer : Module() {

    @Setting(name = "Speed") var speed = 1

    fun getMultiplier(): Int {
        return if (enabled) speed else 1
    }
}