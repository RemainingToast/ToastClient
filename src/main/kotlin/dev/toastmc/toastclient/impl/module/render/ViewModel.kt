package dev.toastmc.toastclient.impl.module.render

import dev.toastmc.toastclient.api.events.HeldItemRenderEvent
import dev.toastmc.toastclient.api.managers.module.Module
import net.minecraft.client.util.math.Vector3f
import org.quantumclient.energy.Subscribe

object ViewModel : Module("ViewModel", Category.RENDER) {

    var fov = number("FOV", 110, 10, 179, 2)

    var scaleX = number("X", 0.75, -1, 2, 2)
    var scaleY = number("Y", 0.60, -1, 2, 2)
    var scaleZ = number("Z", 1, -1, 2, 2)
    var scaleGroup = group("Scale", scaleX, scaleY, scaleZ)

    var posX = number("X", 0, -3, 3, 2)
    var posY = number("Y", 0, -3, 3, 2)
    var posZ = number("Z", 0, -3, 3, 2)
    var posGroup = group("Position", posX, posY, posZ)

    var rotationX = number("X", 0, -180, 180, 2)
    var rotationY = number("Y", 0, -180, 180, 2)
    var rotationZ = number("Z", 0, -180, 180, 2)
    var rotationGroup = group("Rotation", rotationX, rotationY, rotationZ)

    @Subscribe
    fun on(event: HeldItemRenderEvent) {
        val matrices = event.matrixStack

        matrices.scale(scaleX.floatValue, scaleY.floatValue, scaleZ.floatValue)
        matrices.translate(posX.value, posY.value, posZ.value)
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(rotationX.floatValue))
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotationY.floatValue))
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(rotationZ.floatValue))
    }

}