package dev.toastmc.toastclient.impl.gui.hud.components

import dev.toastmc.toastclient.api.util.render.DrawableUtil
import dev.toastmc.toastclient.impl.gui.hud.HUDComponent
import dev.toastmc.toastclient.impl.module.client.ClickGUI
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.EquipmentSlot
import net.minecraft.text.Text
import kotlin.math.roundToInt

object Armor : HUDComponent("Armor") {

    init {
        this.width = 68
        this.height = 16
        val point = getSnapPoint(SnapPoint.BOTTOM_LEFT, width, height)
        this.x = point.x.toDouble()
        this.y = point.y.toDouble()
    }

    override fun render(matrices: MatrixStack) {
        // TODO add horizontal/vertical setting, TODO add a way to see the durability of armor

        mc.itemRenderer.renderGuiItemIcon (
            mc.player!!.getEquippedStack(EquipmentSlot.HEAD),
            x.roundToInt(),
            y.roundToInt()
        )

        mc.player!!.getEquippedStack(EquipmentSlot.HEAD).damage

        mc.itemRenderer.renderGuiItemIcon (
            mc.player!!.getEquippedStack(EquipmentSlot.CHEST),
            x.roundToInt() + 17,
            y.roundToInt()
        )

        mc.itemRenderer.renderGuiItemIcon(
            mc.player!!.getEquippedStack(EquipmentSlot.LEGS),
            x.roundToInt() + 34,
            y.roundToInt()
        )

        mc.itemRenderer.renderGuiItemIcon (
            mc.player!!.getEquippedStack(EquipmentSlot.FEET),
            x.roundToInt() + 50,
            y.roundToInt()
        )

    }

}