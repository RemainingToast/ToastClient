package dev.toastmc.client.gui.click

import com.google.gson.annotations.SerializedName
import java.util.*


class CategorySetting(x: Double, y: Double, expanded: Boolean, expandedModules: ArrayList<String>?) {

    @SerializedName("Pos X")
    private var posX = 0.0

    @SerializedName("Pos Y")
    private var posY = 0.0

    @SerializedName("Expanded")
    private var expanded = false

    @SerializedName("Expanded Modules")
    private var expandedModules: ArrayList<String>? = null

    init {
        posX = x
        posY = y
        this.expanded = expanded
        this.expandedModules = expandedModules
    }

    fun isExpanded(): Boolean {
        return expanded
    }

    fun setExpanded(expanded: Boolean) {
        this.expanded = expanded
    }

    fun getPosX(): Double {
        return posX
    }

    fun setPosX(posx: Double) {
        posX = posx
    }

    fun getPosY(): Double {
        return posY
    }

    fun setPosY(posy: Double) {
        posY = posy
    }

    fun getExpandedModules(): ArrayList<String>? {
        return expandedModules
    }
}