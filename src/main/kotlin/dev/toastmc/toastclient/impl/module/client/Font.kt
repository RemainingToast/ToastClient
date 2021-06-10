package dev.toastmc.toastclient.impl.module.client

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.font.FontAccessor

object Font : Module("Font", Category.CLIENT) {

    private val font = mode("Font", "Raleway", "Raleway").onChanged {
        FontAccessor.fontName = it.value.toLowerCase() + ".ttf"
    }

}