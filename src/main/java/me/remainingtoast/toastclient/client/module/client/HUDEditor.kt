package me.remainingtoast.toastclient.client.module.client

import me.remainingtoast.toastclient.ToastClient
import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module

object HUDEditor : Module("HUDEditor", Category.CLIENT) {

    override fun onEnable() {
        ToastClient.CLICKGUI.enterHUDEditor()
        disable()
    }

}