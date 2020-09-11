package dev.toastmc.client.gui.click

import com.google.gson.annotations.SerializedName
import java.util.*


data class CategorySetting(var x: Double, var y: Double, var expanded: Boolean, var expandedModules: ArrayList<String>)