package dev.toastmc.client.gui.click

import com.google.gson.reflect.TypeToken
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.util.gson
import dev.toastmc.client.util.mc
import net.minecraft.client.MinecraftClient
import java.awt.Color
import java.io.File
import java.io.FileNotFoundException
import java.util.*

open class ClickGuiSettings(private val clickGuiScreen: ClickGuiScreen) {
    private val clickColorsFile = File("toastclient/colors.json")
    private val clickPosFile = File("toastclient/clickgui.json")
    private val defaultOnTextColor = Color(255, 255, 255, 255).rgb
    private val defaultOffTextColor = Color(177, 177, 177, 255).rgb
    private val defaultOffBgColor = Color(0, 0, 0, 64).rgb
    private val defaultOnBgColor = Color(0, 0, 0, 155).rgb
    private val defaultHoverBgColor = Color(131, 212, 252, 92).rgb
    private val defaultBoxColor = Color(0, 0, 0, 255).rgb
    private val defaultPrefixColor = Color(255, 0, 0, 255).rgb
    private val defaultClickColor = Color(121, 205, 255, 128).rgb
    private var categoryPositions: MutableMap<String, CategorySetting> = TreeMap()
    var colors = Colors()

    init {
        loadColors()
        loadPositions()
    }

    fun getPositions(category: String): CategorySetting {
        var setting = categoryPositions[category]
        if (setting == null) {
            loadPositions()
            setting = categoryPositions[category]
            if (setting == null) initCategoryPositions()
        }
        return categoryPositions[category]!!
    }

    fun initCategoryPositions() {
        if (mc.window == null) return
        var i = 0
        var y = 5
        for (category in Category.values()) {
            if (category == Category.NONE) continue
            var x: Int = 5 + clickGuiScreen.w.times(i) + 5 * i
            if (x + clickGuiScreen.w + 30 > MinecraftClient.getInstance().window.width / 2) {
                y += MinecraftClient.getInstance().window.height / 2 / 3
                x = 100
                i = 0
            }
            (categoryPositions as TreeMap<String, CategorySetting>)[category.toString()] = CategorySetting(x.toDouble(), y.toDouble(), false, ArrayList())
            i++
        }
    }

    fun loadPositions() {
        try {
            categoryPositions = TreeMap()
            val loadedData: MutableMap<String?, CategorySetting?>? = gson.fromJson(clickPosFile.readText(), object : TypeToken<Map<String?, CategorySetting?>?>() {}.type)
            if (loadedData != null) {
                for (item in loadedData) if (item.key != null || item.value != null) categoryPositions.putIfAbsent(
                    item.key!!,
                    item.value!!
                )
                return
            }
        } catch (_: FileNotFoundException) {
            println("Failed to load GUI positions")
        } finally {
            initCategoryPositions()
            savePositions()
        }
    }

    fun moduleExpanded(cat: String, mod: Module): Boolean {
        return getPositions(cat).expandedModules.contains(mod.label)
    }


    open fun savePositions() {
        clickPosFile.createNewFile()
        clickPosFile.writeText(gson.toJson(categoryPositions))
    }

    fun saveColors() {
        clickColorsFile.createNewFile()
        clickColorsFile.writeText(gson.toJson(colors))
    }

    private fun allDefaults() {
        colors = Colors()
        setCategoryDefaults()
        setDescriptionDefaults()
        setModuleDefaults()
        setSettingsDefaults()
    }

    private fun setCategoryDefaults() {
        colors.categoryBgColor = defaultOffBgColor
        colors.categoryBoxColor = defaultBoxColor
        colors.categoryClickColor = defaultClickColor
        colors.categoryHoverBgColor = defaultHoverBgColor
        colors.categoryPrefix = "+ "
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
        colors.moduleOffBgColor = defaultOffBgColor
        colors.moduleOffTextColor = defaultOffTextColor
        colors.moduleOnBgColor = defaultOnBgColor
        colors.moduleExpandadBgColor = defaultOnBgColor
        colors.moduleOnTextColor = defaultOnTextColor
        colors.modulePrefix = " > "
        colors.modulePrefixColor = defaultPrefixColor
    }

    private fun setSettingsDefaults() {
        colors.settingBoxColor = defaultBoxColor
        colors.settingClickColor = defaultClickColor
        colors.settingHoverBgColor = defaultHoverBgColor
        colors.settingOffBgColor = defaultOnBgColor
        colors.settingOffTextColor = defaultOffTextColor
        colors.settingOnBgColor = defaultOnBgColor
        colors.settingOnTextColor = defaultOnTextColor
        colors.settingSliderBarColor = defaultOffTextColor
        colors.settingSliderKnobColor = defaultOnTextColor
        colors.settingSliderKnobHoverColor = defaultOnTextColor
        colors.settingSliderSideNumbersColor = defaultOnTextColor
        colors.settingPrefix = " "
        colors.settingPrefixColor = defaultPrefixColor
    }

    fun loadColors() {
        try {
            val newColors: Colors? = gson.fromJson(clickColorsFile.readText(), object : TypeToken<Colors?>() {}.type)
            if (newColors != null) {
                colors = newColors
                return
            }
        } catch (_: FileNotFoundException) {
        } finally {
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
        var moduleExpandadBgColor = 0
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