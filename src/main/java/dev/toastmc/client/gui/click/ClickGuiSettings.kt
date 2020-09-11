package dev.toastmc.client.gui.click

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dev.toastmc.client.ToastClient.Companion.FILE_MANAGER
import dev.toastmc.client.module.Category
import net.minecraft.client.MinecraftClient
import java.awt.Color
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.util.*
import kotlin.collections.ArrayList


class ClickGuiSettings {
    private val clickColorsFile = "gui-colors.json"
    private val clickPosFile = "click-gui-positions.json"

    private val defaultOnTextColor: Int = Color(255, 255, 255, 255).rgb
    private val defaultOffTextColor: Int = Color(177, 177, 177, 255).rgb
    private val defaultBgColor: Int = Color(0, 0, 0, 64).rgb
    private val defaultHoverBgColor: Int = Color(131, 212, 252, 92).rgb
    private val defaultBoxColor: Int = Color(0, 0, 0, 255).rgb
    private val defaultPrefixColor: Int = Color(8, 189, 8, 255).rgb
    private val defaultClickColor: Int = Color(121, 205, 255, 128).rgb

    private var gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private var categoryPositions: MutableMap<String, CategorySetting> = TreeMap()
    var colors: Colors = Colors()

    private var guiScreen = ClickGuiScreen()

    init {
        loadColors()
        loadPositions()
    }

    fun getPositions(category: String?): CategorySetting {
        var setting = categoryPositions[category]
        if (setting == null) {
            loadPositions()
            setting = categoryPositions[category]
            if (setting == null) initCategoryPositions()
        }
        return categoryPositions[category]!!
    }

    private fun initCategoryPositions() {
        var i = 0
        var y = 5
        for (category in Category.values()) {
            var x: Int = 5 + guiScreen.width.times(i) + 5 * i
            if (x + guiScreen.width + 10 > MinecraftClient.getInstance().window.width / 2) {
                y += MinecraftClient.getInstance().window.height / 2 / 3
                x = 5
                i = 0
            }
            (categoryPositions as TreeMap<String, CategorySetting>)[category.toString()] =
                CategorySetting(x.toDouble(), y.toDouble(), false, ArrayList())
            i++
        }
    }

    fun loadPositions() {
        try {
            categoryPositions = gson.fromJson(
                FileReader(FILE_MANAGER.createFile(File(clickPosFile))),
                object : TypeToken<Map<String?, CategorySetting?>?>() {}.type
            )
        } catch (e: FileNotFoundException) {
            initCategoryPositions()
            savePositions()
        }
    }

    fun savePositions() {
        gson.toJson(categoryPositions)?.let { FILE_MANAGER.writeFile(File(clickPosFile), it) }
    }

    fun saveColors() {
        gson.toJson(colors)?.let { FILE_MANAGER.writeFile(File(clickColorsFile), it) }
    }

    private fun allDefaults() {
        colors = Colors()
        setCategoryDefaults()
        setDescriptionDefaults()
        setModuleDefaults()
        setSettingsDefaults()
    }

    private fun setCategoryDefaults() {
        colors.categoryBgColor = defaultBgColor
        colors.categoryBoxColor = defaultBoxColor
        colors.categoryClickColor = defaultClickColor
        colors.categoryHoverBgColor = defaultHoverBgColor
        colors.categoryPrefix = "> "
        colors.categoryPrefixColor = defaultPrefixColor
        colors.categoryTextColor = defaultOnTextColor
    }

    private fun setDescriptionDefaults() {
        colors.descriptionBgColor = Color(83, 83, 83, 255).rgb
        colors.descriptionBoxColor = defaultBoxColor
        colors.descriptionPrefix = ""
        colors.descriptionPrefixColor = defaultPrefixColor
        colors.descriptionTextColor = defaultOnTextColor
    }

    private fun setModuleDefaults() {
        colors.moduleBoxColor = defaultBoxColor
        colors.moduleClickColor = defaultClickColor
        colors.moduleHoverBgColor = defaultHoverBgColor
        colors.moduleOffBgColor = defaultBgColor
        colors.moduleOffTextColor = defaultOffTextColor
        colors.moduleOnBgColor = defaultBgColor
        colors.moduleOnTextColor = defaultOnTextColor
        colors.modulePrefix = " > "
        colors.modulePrefixColor = defaultPrefixColor
    }

    private fun setSettingsDefaults() {
        colors.settingBoxColor = defaultBoxColor
        colors.settingClickColor = defaultClickColor
        colors.settingHoverBgColor = defaultHoverBgColor
        colors.settingOffBgColor = defaultBgColor
        colors.settingOffTextColor = defaultOffTextColor
        colors.settingOnBgColor = defaultBgColor
        colors.settingOnTextColor = defaultOnTextColor
        colors.settingSliderBarColor = defaultOffTextColor
        colors.settingSliderKnobColor = defaultOnTextColor
        colors.settingSliderKnobHoverColor = defaultOnTextColor
        colors.settingSliderSideNumbersColor = defaultOnTextColor
        colors.settingPrefix = "    "
        colors.settingPrefixColor = defaultPrefixColor
    }

    fun loadColors() {
        try {
            colors = gson.fromJson(
                FileReader(FILE_MANAGER.createFile(File(clickColorsFile))),
                object : TypeToken<Colors?>() {}.type
            )!!
        } catch (e: FileNotFoundException) {
            allDefaults()
            saveColors()
        }
    }

    class Colors {
        var categoryPrefix = ""
        var categoryTextColor = 0
        var categoryHoverBgColor = 0
        var categoryBgColor = 0
        var categoryBoxColor = 0
        var categoryPrefixColor = 0
        var categoryClickColor = 0
        var descriptionPrefix = ""
        var descriptionTextColor = 0
        var descriptionBoxColor = 0
        var descriptionBgColor = 0
        var descriptionPrefixColor = 0
        var modulePrefix = ""
        var moduleOnTextColor = 0
        var moduleOnBgColor = 0
        var moduleOffTextColor = 0
        var moduleOffBgColor = 0
        var moduleHoverBgColor = 0
        var moduleBoxColor = 0
        var modulePrefixColor = 0
        var moduleClickColor = 0
        var settingPrefix = ""
        var settingOnTextColor = 0
        var settingSliderBarColor = 0
        var settingSliderKnobColor = 0
        var settingSliderKnobHoverColor = 0
        var settingSliderSideNumbersColor = 0
        var settingOnBgColor = 0
        var settingOffTextColor = 0
        var settingOffBgColor = 0
        var settingHoverBgColor = 0
        var settingBoxColor = 0
        var settingPrefixColor = 0
        var settingClickColor = 0
    }
}