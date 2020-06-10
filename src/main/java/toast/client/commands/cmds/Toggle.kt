package toast.client.commands.cmds

import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.Logger

/**
 * Command to toggle modules
 */
class Toggle : Command("Toggle", """${ToastClient.cmdPrefix}toggle [module]""", "Toggles the specified module", false, "toggle", "t") {
    override fun run(args: Array<String>) {
        if (args.isEmpty()) {
            Logger.message("Please provide a module name", Logger.WARN, true)
        } else {
            for (module in ToastClient.MODULE_MANAGER.modules) {
                if (module.name.replace(" ".toRegex(), "").toLowerCase() == args[0].toLowerCase()) {
                    module.toggle()
                    if (module.enabled) {
                        Logger.message("""Enabled ${module.name}""", Logger.INFO, true)
                    } else {
                        Logger.message("""Disabled ${module.name}""", Logger.INFO, true)
                    }
                    return
                }
            }
            Logger.message("""Could not find module ${args[0]}""", Logger.WARN, true)
        }
    }
}