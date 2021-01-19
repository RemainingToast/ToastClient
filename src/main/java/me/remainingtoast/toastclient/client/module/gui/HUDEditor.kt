package me.remainingtoast.toastclient.client.module.gui

import me.remainingtoast.toastclient.ToastClient
import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module

class HUDEditor : Module("HUDEditor", Category.GUI) {

    override fun onEnable() {
        ToastClient.CLICKGUI.enterHUDEditor()
        disable()
    }

}