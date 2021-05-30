package dev.toastmc.toastclient.impl.module.client

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.font.FontRenderer

object Font : Module("Font", Category.CLIENT) {

    var fontName = mode("FontName", "Minecraft", listOf("Raleway")).onChanged { update() }
    var fontSize = number("FontSize", 24.0, 5.0, 50.0).onChanged { update() }

    var renderer: FontRenderer? = null

    private fun update() {
        if(mc.world != null && mc.player != null) {
            renderer = create()
        }
    }

    fun create(): FontRenderer {
        return FontRenderer.create(fontName.value, fontSize.intValue, false, false, false)
    }

}