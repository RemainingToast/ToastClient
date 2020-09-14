package dev.toastmc.client.module.movement

import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.mc
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting

// https://github.com/MineGame159/meteor-client/blob/master/src/main/java/minegame159/meteorclient/modules/misc/Timer.java
// LICENSE: https://github.com/MineGame159/meteor-client/blob/master/LICENSE
@ModuleManifest(
    label = "Timer",
    description = "Speed up client speed",
    category = Category.MOVEMENT
)
class Timer : Module() {

    @Setting(name = "Speed") var speed = 1
    @Setting(name = "GroundOnly") var ground = false

    fun getMultiplier(): Int {
        return if (enabled) speed else 1
    }

    fun getOnGroundCheck(): Boolean {
        return if(ground) mc.player!!.isOnGround else ground
    }
}