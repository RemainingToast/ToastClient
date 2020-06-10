package toast.client.commands.cmds

import org.apache.commons.lang3.math.NumberUtils
import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.modules.Module
import toast.client.modules.config.Setting
import toast.client.utils.Logger
import java.util.*

/**
 * Command to list and change configuration options for modules
 */
class Set : Command("Set", """${ToastClient.cmdPrefix}set <module> [setting] [newvalue]""", "Changes module settings", false, "set", "config", "settings") {
    private fun displaySetting(name: String, setting: Setting, module: Module) {
        when (setting.type) {
            0 -> {
                var modes = StringBuilder()
                module.settings.getModes(name)!!.forEach { mode ->
                    modes.append(mode).append(", ")
                }
                modes = StringBuilder(modes.subSequence(0, modes.length - 3) as String)
                Logger.message(""" Mode: $name, current: ${setting.getMode()} available: $modes""", Logger.EMPTY, false)
            }
            1 -> Logger.message(""" Value: $name, current: ${setting.getValue()} minimum: ${module.settings.getSettingDef(name)!!.minValue} maximum: ${module.settings.getSettingDef(name)!!.maxValue}""", Logger.EMPTY, false)
            2 -> Logger.message(""" Toggle: $name, state: ${if (setting.isEnabled()) "enabled" else "disabled"}""", Logger.EMPTY, false)
            else -> Logger.message("Invalid setting", Logger.ERR, false)
        }
    }

    override fun run(args: Array<String>) = if (args.isNotEmpty()) {
        val module = ToastClient.MODULE_MANAGER.getModule(args[0])
        if (module != null) {
            if (args.size > 1) {
                val setting = module.settings.getSetting(args[1])
                if (setting != null) {
                    if (args.size == 2) {
                        displaySetting(args[1], setting, module)
                    } else {
                        val settingDef = module.settings.getSettingDef(args[1])
                        when (setting.type) {
                            0 -> {
                                if (settingDef!!.modes!!.contains(args[2])) {
                                    setting.setMode(args[2])
                                    Logger.message("""Changed value of setting ${args[1]} to ${args[2]}""", Logger.INFO, false)
                                } else {
                                    Logger.message("""${args[2]} is an invalid value for this setting.""", Logger.WARN, false)
                                }
                            }
                            1 -> {
                                if (NumberUtils.isParsable(args[2])) {
                                    val newNum = args[2].toDouble()
                                    if (newNum <= settingDef!!.maxValue!!) {
                                        if (newNum >= settingDef.minValue!!) {
                                            setting.setValue(newNum)
                                            Logger.message("""Changed value of setting ${args[1]} to ${args[2]}""", Logger.INFO, false)
                                        } else {
                                            Logger.message("""$newNum is too small, the minimum value is: ${settingDef.minValue}""", Logger.WARN, false)
                                        }
                                    } else {
                                        Logger.message("""$newNum is too big, the maximum value is: ${settingDef.maxValue}""", Logger.WARN, false)
                                    }
                                } else {
                                    Logger.message("""${args[2]} is an invalid value for this setting, please give a number.""", Logger.WARN, false)
                                }
                            }
                            2 -> {
                                if (args[2] == "true" || args[2] == "false") {
                                    setting.setEnabled(args[2].toBoolean())
                                    Logger.message("""Changed value of setting ${args[1]} to ${args[2]}""", Logger.INFO, false)
                                } else {
                                    Logger.message("""${args[2]} is an invalid value for this setting, please give a boolean (true/false).""", Logger.WARN, false)
                                }
                            }
                            else -> {
                                Logger.message("Internal programming error.", Logger.WARN, false)
                            }
                        }
                    }
                } else {
                    Logger.message(args[1] + " is not a setting.", Logger.WARN, false)
                }
            } else {
                Logger.message("Setting(s) for module " + module.name + ": ", Logger.INFO, false)
                for ((key, value) in module.settings.getSettings()) {
                    displaySetting(key, value, module)
                }
            }
        } else {
            Logger.message(args[0] + " is not a module.", Logger.WARN, false)
        }
    } else {
        Logger.message("Invalid arguments.", Logger.WARN, false)
    }
}