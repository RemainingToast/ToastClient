package toast.client.commands.cmds

import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.modules.Module
import toast.client.utils.Logger

/**
 * Command to list all of the categories or the modules in a category
 */
class ListModules : Command("List", """${ToastClient.cmdPrefix}list [category]""", "Lists categories or modules in a category", false, "cat", "list", "mods") {
    override fun run(args: Array<String>) {
        if (args.isEmpty()) {
            Logger.message("List of categories:", Logger.INFO, true)
            for (category in Module.Category.values()) {
                Logger.message("""  ${category.name}""", Logger.EMPTY, true)
            }
        } else {
            for (category in Module.Category.values()) {
                if (category.name.equals(args[0], ignoreCase = true)) {
                    Logger.message("Modules in ${args[0]}:", Logger.INFO, true)
                    for (module in ToastClient.MODULE_MANAGER.getModulesInCategory(Module.Category.valueOf(args[0].toUpperCase()))) {
                        Logger.message("  ${module.name}: ${module.description}", Logger.EMPTY, true)
                    }
                    return
                }
            }
            Logger.message("${args[0]} is not a valid category.", Logger.WARN, true)
        }
    }
}