package toast.client.commands.cmds

import net.minecraft.util.Formatting
import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.Logger

/**
 * Command to reload one or all of the mod's configurations from it's file
 */
class Reload : Command("Reload", """${ToastClient.cmdPrefix}reload [config]""", "Reloads all or one of the configuration files", false, "reload", "rl") {
    override fun run(args: Array<String>) {
        if (args.isEmpty()) {
            ToastClient.CONFIG_MANAGER.loadConfig()
            ToastClient.CONFIG_MANAGER.loadKeyBinds()
            ToastClient.CONFIG_MANAGER.loadModules()
            if (ToastClient.clickGuiHasOpened) {
                ToastClient.clickGui.reloadConfig()
            }
            Logger.message("Reloaded all configuration files.", Logger.INFO, true)
        } else {
            when (args[0]) {
                "config" -> {
                    ToastClient.CONFIG_MANAGER.loadConfig()
                    Logger.message("Reloaded module options.", Logger.INFO, true)
                }
                "modules" -> {
                    ToastClient.CONFIG_MANAGER.loadModules()
                    Logger.message("Reloaded modules.", Logger.INFO, true)
                }
                "keybinds" -> {
                    ToastClient.CONFIG_MANAGER.loadKeyBinds()
                    Logger.message("Reloaded keybinds.", Logger.INFO, true)
                }
                "clickgui" -> if (ToastClient.clickGuiHasOpened) {
                    ToastClient.clickGui.reloadConfig()
                } else {
                    Logger.message("ClickGUI hasn't been opened yet", Logger.ERR, true)
                }
                else -> {
                    Logger.message("Invalid argument, valid arguments are:", Logger.WARN, true)
                    Logger.message("${Formatting.GRAY}  modules ${Formatting.YELLOW}reloads the enabled state of modules", Logger.EMPTY, true)
                    Logger.message("${Formatting.GRAY}  keybinds ${Formatting.YELLOW}reloads all keybinds", Logger.EMPTY, true)
                    Logger.message("${Formatting.GRAY}  clickgui ${Formatting.YELLOW}reloads the clickgui", Logger.EMPTY, true)
                    Logger.message("${Formatting.GRAY}  config ${Formatting.YELLOW}reloads client config (not modules)", Logger.EMPTY, true)
                }
            }
        }
    }
}