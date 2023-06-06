package dev.toastmc.toastclient.impl.module.render

import dev.toastmc.toastclient.api.events.HeldItemRenderEvent
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.ToastColor
import net.minecraft.util.math.RotationAxis
import org.quantumclient.energy.Subscribe

object ViewModel : Module("ViewModel", Category.RENDER) {

    var rainbowHand = bool("RainbowHand", true)
    var handColor = ToastColor.rainbow(1)

    var scaleX = number("X", 1, 0.5, 1.5, 5)
    var scaleY = number("Y", 1, 0.5, 1.5, 5)
    var scaleZ = number("Z", 1, 0.5, 1.5, 5)
    var scaleGroup = group("Scale", scaleX, scaleY, scaleZ)

    var posX = number("X", 0, -0.5, 0.5, 5)
    var posY = number("Y", 0, -0.5, 0.5, 5)
    var posZ = number("Z", 0, -0.5, 0.5, 5)
    var posGroup = group("Position", posX, posY, posZ)

    var rotationX = number("X", 0, -180, 180, 5)
    var rotationY = number("Y", 0, -180, 180, 5)
    var rotationZ = number("Z", 0, -180, 180, 5)
    var rotationGroup = group("Rotation", rotationX, rotationY, rotationZ)

    @Subscribe
    fun on(event: HeldItemRenderEvent) {
        if (!isEnabled()) {
            return
        }

        // TODO: fix offhand - @see HeldItemRenderer#renderArm instead
        if (rainbowHand.value && (mc.player!!.handItems.all { it.isEmpty } /*|| event.hand == Hand.MAIN_HAND && event.item.isEmpty*/))
        {
            handColor = ToastColor.rainbow(1)
            handColor.glColor()
        }

        val matrices = event.matrixStack
        matrices.scale(scaleX.floatValue, scaleY.floatValue, scaleZ.floatValue)
        matrices.translate(posX.value, posY.value, posZ.value)
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationX.floatValue))
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationY.floatValue))
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotationZ.floatValue))
    }
}