package me.remainingtoast.toastclient.api.module

import com.lukflug.panelstudio.FixedComponent
import com.lukflug.panelstudio.theme.Theme
import me.remainingtoast.toastclient.ToastClient
import java.awt.Point


abstract class HUDModule(title: String, defaultPos: Point) : Module(title, Category.HUD) {

    abstract var component: FixedComponent
    val position: Point = defaultPos

    abstract fun populate(theme: Theme)

    open fun resetPosition() {
        component.setPosition(ToastClient.CLICKGUI.guiInterface, position)
    }
}