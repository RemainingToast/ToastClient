package toast.client.commands.cmds

import net.minecraft.util.Formatting
import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.MessageUtil

/**
 * Command to save all or one of the mod's configurations to it's file
 */
class Save : Command("Save", """${ToastClient.cmdPrefix}save [config]""", "Saves the configuration files", false, "save") {
    override fun run(args: Array<String>) {
        if (args.isEmpty()) {
            ToastClient.CONFIG_MANAGER.writeConfig()
            ToastClient.CONFIG_MANAGER.writeKeyBinds()
            ToastClient.CONFIG_MANAGER.writeModules()
            MessageUtil.sendMessage("Saved all configuration files.", MessageUtil.Color.GREEN)
        } else {
            when (args[0]) {
                "config" -> {
                    ToastClient.CONFIG_MANAGER.writeConfig()
                    MessageUtil.sendMessage("Saved client config (not modules).", MessageUtil.Color.GREEN)
                }
                "modules" -> {
                    ToastClient.CONFIG_MANAGER.writeModules()
                    MessageUtil.sendMessage("Saved config.", MessageUtil.Color.GREEN)
                }
                "keybinds" -> {
                    ToastClient.CONFIG_MANAGER.writeKeyBinds()
                    MessageUtil.sendMessage("Saved keybinds.",  MessageUtil.Color.GREEN)
                }
                "macros" -> {
                    ToastClient.CONFIG_MANAGER.writeMacros()
                    MessageUtil.sendMessage("Saved macros.", MessageUtil.Color.GREEN)
                }
                else -> {
                    MessageUtil.sendMessage("Invalid argument, valid arguments are:", MessageUtil.Color.GREEN)
                    MessageUtil.sendMessage("${Formatting.GRAY}  modules ${Formatting.YELLOW}saves the enabled state of modules", MessageUtil.Color.GRAY)
                    MessageUtil.sendMessage("${Formatting.GRAY}  keybinds ${Formatting.YELLOW}saves all keybinds", MessageUtil.Color.GRAY)
                    MessageUtil.sendMessage("${Formatting.GRAY}  macros ${Formatting.YELLOW}saves macros", MessageUtil.Color.GRAY)
                    MessageUtil.sendMessage("${Formatting.GRAY}  config ${Formatting.YELLOW}saves client config (not modules)", MessageUtil.Color.GRAY)
                }
            }
        }
    }
}