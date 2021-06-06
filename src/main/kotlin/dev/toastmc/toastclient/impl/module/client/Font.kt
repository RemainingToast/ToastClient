package dev.toastmc.toastclient.impl.module.client

import dev.toastmc.toastclient.api.managers.module.Module
import net.minecraft.util.Identifier

object Font : Module("Font", Category.CLIENT) {

    private val font = mode("Font", "Minecraft", "Minecraft", "Raleway")

    fun getFont(): Identifier {
        return if (isEnabled()) {
            when (font.value) {
                "Raleway" -> Identifier("toastclient", "raleway")
                else -> Identifier("minecraft", "default")
            }
        } else Identifier("minecraft", "default")
    }

}