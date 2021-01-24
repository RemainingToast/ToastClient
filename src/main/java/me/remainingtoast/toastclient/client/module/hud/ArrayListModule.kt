package me.remainingtoast.toastclient.client.module.hud

import com.lukflug.panelstudio.FixedComponent
import com.lukflug.panelstudio.hud.HUDList
import com.lukflug.panelstudio.hud.ListComponent
import com.lukflug.panelstudio.theme.Theme
import me.remainingtoast.toastclient.ToastClient
import me.remainingtoast.toastclient.api.module.HUDModule
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.setting.type.BooleanSetting
import me.remainingtoast.toastclient.api.setting.type.ColorSetting
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Formatting
import java.awt.Color
import java.awt.Color.RGBtoHSB
import java.awt.Point
import java.util.*

object ArrayListModule : HUDModule("ArrayList", Point(5, 5)) {

    override lateinit var component: FixedComponent
    private var list: ModuleList = ModuleList()

    var color: ColorSetting = registerColor("Color", "Array List Color", true, false, Color.RED, false)
    var sortUp: BooleanSetting = registerBoolean("Sort Up", true)
    var sortRight: BooleanSetting = registerBoolean("Sort Right", false)

    override fun populate(theme: Theme) {
        component = ListComponent(name, theme.panelRenderer, position, list)
    }

    override fun onOverlayRender() {
        list.activeModules.clear()
        for(m in ToastClient.MODULE_MANAGER.modules){
            if(m.isEnabled() && m.isDrawn() && m !is HUDModule) list.activeModules.add(m)
        }
        list.activeModules.sortWith(Comparator.comparing { module: Module -> MinecraftClient.getInstance().textRenderer.getWidth("${module.name+Formatting.GRAY} ${module.getHudInfo()}")})
    }

    private class ModuleList : HUDList {
        var activeModules: MutableList<Module> = ArrayList()

        override fun getSize(): Int {
            return activeModules.size
        }

        override fun getItem(index: Int): String {
            val module = activeModules[index]
            return module.name + Formatting.GRAY.toString() + " " + module.getHudInfo()
        }

        override fun getItemColor(index: Int): Color {
            val hue = RGBtoHSB(color.value.red, color.value.green, color.value.blue, null)[0]
            val sat = RGBtoHSB(color.value.red, color.value.green, color.value.blue, null)[1]
            val bri = RGBtoHSB(color.value.red, color.value.green, color.value.blue, null)[2];
            val r = if (color.rainbow) .02f * index else 0f
            return Color.getHSBColor(hue + r, sat, bri)
        }

        override fun sortUp(): Boolean {
            return sortUp.isOn
        }

        override fun sortRight(): Boolean {
            return sortRight.isOn
        }
    }
}