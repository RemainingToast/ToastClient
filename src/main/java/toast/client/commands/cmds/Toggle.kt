package toast.client.commands.cmds

import net.minecraft.util.Formatting
import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.MessageUtil




/**
 * Command to toggle modules
 */
class Toggle : Command("Toggle", """${ToastClient.cmdPrefix}toggle [module]""", "Toggles the specified module", false, "toggle", "t") {
    override fun run(args: Array<String>) {
        if (args.isEmpty()) {
            MessageUtil.sendMessage("Please provide a module name", MessageUtil.Color.RED)
        } else {
            for (module in ToastClient.MODULE_MANAGER.modules) {
                if (module.name.replace(" ".toRegex(), "").toLowerCase() == args[0].toLowerCase()) {
                    module.toggle()
                    ToastClient.CONFIG_MANAGER.writeModules()
                    MessageUtil.sendMessage("Toggled ${module.name} ${if (module.enabled) "${Formatting.RED} OFF" else "${Formatting.GREEN} ON"}", MessageUtil.Color.GRAY)
                    return
                }
            }
            MessageUtil.sendMessage("""Could not find module ${args[0]}""", MessageUtil.Color.RED)
        }
    }
}