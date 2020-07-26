package toast.client.commands.cmds

import org.apache.commons.lang3.math.NumberUtils
import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.modules.Module
import toast.client.modules.config.Setting
import toast.client.utils.MessageUtil

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
                MessageUtil.sendMessage(""" Mode: $name, current: ${setting.getMode()} available: $modes""", MessageUtil.Color.GRAY)
            }
            1 -> MessageUtil.sendMessage(""" Value: $name, current: ${setting.getValue()} minimum: ${module.settings.getSettingDef(name)!!.minValue} maximum: ${module.settings.getSettingDef(name)!!.maxValue}""", MessageUtil.Color.GRAY)
            2 -> MessageUtil.sendMessage(""" Toggle: $name, state: ${if (setting.isEnabled()) "enabled" else "disabled"}""", MessageUtil.Color.GRAY)
            else -> MessageUtil.sendMessage("Invalid setting", MessageUtil.Color.RED)
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
                                    MessageUtil.sendMessage("""Changed value of setting ${args[1]} to ${args[2]}""", MessageUtil.Color.GRAY)
                                } else {
                                    MessageUtil.sendMessage("""${args[2]} is an invalid value for this setting.""", MessageUtil.Color.RED)
                                }
                            }
                            1 -> {
                                if (NumberUtils.isParsable(args[2])) {
                                    val newNum = args[2].toDouble()
                                    if (newNum <= settingDef!!.maxValue!!) {
                                        if (newNum >= settingDef.minValue!!) {
                                            setting.setValue(newNum)
                                            MessageUtil.sendMessage("""Changed value of setting ${args[1]} to ${args[2]}""", MessageUtil.Color.GREEN)
                                        } else {
                                            MessageUtil.sendMessage("""$newNum is too small, the minimum value is: ${settingDef.minValue}""", MessageUtil.Color.RED)
                                        }
                                    } else {
                                        MessageUtil.sendMessage("""$newNum is too big, the maximum value is: ${settingDef.maxValue}""", MessageUtil.Color.RED)
                                    }
                                } else {
                                    MessageUtil.sendMessage("""${args[2]} is an invalid value for this setting, please give a number.""", MessageUtil.Color.RED)
                                }
                            }
                            2 -> {
                                if (args[2] == "true" || args[2] == "false") {
                                    setting.setEnabled(args[2].toBoolean())
                                    MessageUtil.sendMessage("""Changed value of setting ${args[1]} to ${args[2]}""", MessageUtil.Color.RED)
                                } else {
                                    MessageUtil.sendMessage("""${args[2]} is an invalid value for this setting, please give a boolean (true/false).""", MessageUtil.Color.RED)
                                }
                            }
                            else -> {
                                MessageUtil.sendMessage("Internal programming error.", MessageUtil.Color.RED)
                            }
                        }
                    }
                } else {
                    MessageUtil.sendMessage("${args[1]} is not a setting.", MessageUtil.Color.RED)
                }
            } else {
                MessageUtil.sendMessage("Setting(s) for module " + module.name + ": ", MessageUtil.Color.GRAY)
                for ((key, value) in module.settings.getSettings()) {
                    displaySetting(key, value, module)
                }
            }
        } else {
            MessageUtil.sendMessage("${args[0]} is not a module.", MessageUtil.Color.RED)
        }
    } else {
        MessageUtil.sendMessage("Invalid arguments.", MessageUtil.Color.RED)
    }
}