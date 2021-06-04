package dev.toastmc.toastclient.api.util

import net.minecraft.util.math.Vec3d
import java.util.concurrent.ThreadLocalRandom

object RandomUtil {
    fun getRandom(): ThreadLocalRandom {
        return ThreadLocalRandom.current()
    }

    fun nextInt(min: Int = 0, max: Int = 1): Int {
        return getRandom().nextInt(max - min) + min
    }

    fun nextDouble(min: Double = 0.0, max: Double = 1.0): Double {
        return getRandom().nextDouble() * (max - min) + min
    }

    fun nextFloat(min: Float = 0.0F, max: Float = 1.0F): Float {
        return getRandom().nextFloat() * (max - min) + min
    }

    fun nextCloud(radius: Double, quantity: Int, center: Vec3d = Vec3d.ZERO): List<Vec3d> {
        val cloud = mutableListOf<Vec3d>()
        while (cloud.size < quantity) {
            val potential = center.add(Vec3d(nextDouble(-radius, radius), nextDouble(-radius, radius), nextDouble(-radius, radius)))
            if (potential.distanceTo(center) <= radius) {
                cloud.add(potential)
            }
        }
        return cloud
    }
}