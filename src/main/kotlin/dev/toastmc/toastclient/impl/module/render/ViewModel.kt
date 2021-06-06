package dev.toastmc.toastclient.impl.module.render

import dev.toastmc.toastclient.api.events.HeldItemRenderEvent
import dev.toastmc.toastclient.api.managers.module.Module
import net.minecraft.client.util.math.Vector3f
import org.quantumclient.energy.Subscribe

object ViewModel : Module("ViewModel", Category.RENDER) {

    var fov = number("FOV", 110, 10, 179, 0.1)
    var itemFov = bool("Items", true).onChanged { bool ->
        scaleGroup.isHidden = !bool.value
        posGroup.isHidden = !bool.value
        rotationGroup.isHidden = !bool.value
    }

    var scaleX = number("SX", 0.75, -1, 2, 0.1)
    var scaleY = number("SY", 0.60, -1, 2, 0.1)
    var scaleZ = number("SZ", 1, -1, 2, 0.1)
    var scaleGroup = group("Scale", scaleX, scaleY, scaleZ)

    var posX = number("PX", 0, -3, 3, 0.1)
    var posY = number("PY", 0, -3, 3, 0.1)
    var posZ = number("PZ", 0, -3, 3, 0.1)
    var posGroup = group("Position", posX, posY, posZ)

    var rotationX = number("RX", 0, -180, 180, 0.1)
    var rotationY = number("RY", 0, -180, 180, 0.1)
    var rotationZ = number("RZ", 0, -180, 180, 0.1)
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