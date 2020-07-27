package dev.toastmc.client.event.events

import dev.toastmc.client.event.ToastEvent
import net.minecraft.client.gui.screen.Screen

open class ScreenEvent(var screen: Screen?) : ToastEvent() {

    class Displayed(screen: Screen?) : ScreenEvent(screen)
    class Closed(screen: Screen?) : ScreenEvent(screen)

}