package toast.client.commands.cmds

import net.minecraft.util.Formatting
import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.Logger

class Save : Command("Save", """${ToastClient.cmdPrefix}save [config]""", "Saves the configuration files", false, "save") {
    override fun run(args: Array<String>) {
        if (args.isEmpty()) {
            ToastClient.CONFIG_MANAGER.writeConfig()
            ToastClient.CONFIG_MANAGER.writeKeyBinds()
            ToastClient.CONFIG_MANAGER.writeModules()
            Logger.message("Saved all configuration files.", Logger.INFO, true)
        } else {
            when (args[0]) {
                "config" -> {
                    ToastClient.CONFIG_MANAGER.writeConfig()
                    Logger.message("Saved client config (not modules).", Logger.INFO, true)
                }
                "modules" -> {
                    ToastClient.CONFIG_MANAGER.writeModules()
                    Logger.message("Saved modules.", Logger.INFO, true)
                }
                "keybinds" -> {
                    ToastClient.CONFIG_MANAGER.writeKeyBinds()
                    Logger.message("Saved keybinds.", Logger.INFO, true)
                }
                "macros" -> {
                    ToastClient.CONFIG_MANAGER.writeMacros()
                    Logger.message("Saved macros.", Logger.INFO, true)
                }
                else -> {
                    Logger.message("Invalid argument, valid arguments are:", Logger.WARN, true)
                    Logger.message("${Formatting.GRAY}  modules ${Formatting.YELLOW}saves the enabled state of modules", Logger.EMPTY, true)
                    Logger.message("${Formatting.GRAY}  keybinds ${Formatting.YELLOW}saves all keybinds", Logger.EMPTY, true)
                    Logger.message("${Formatting.GRAY}  macros ${Formatting.YELLOW}saves macros", Logger.EMPTY, true)
                    Logger.message("${Formatting.GRAY}  config ${Formatting.YELLOW}saves client config (not modules)", Logger.EMPTY, true)
                }
            }
        }
    }
}