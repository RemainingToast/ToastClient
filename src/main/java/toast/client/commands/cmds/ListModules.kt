package toast.client.commands.cmds

import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.modules.Module
import toast.client.utils.MessageUtil


/**
 * Command to list all of the categories or the modules in a category
 */
class ListModules : Command("List", """${ToastClient.cmdPrefix}list [category]""", "Lists categories or modules in a category", false, "cat", "list", "mods") {
    override fun run(args: Array<String>) {
        val sb = StringBuilder()
        if (args.isEmpty()) {
            MessageUtil.sendMessage("List of categories:", MessageUtil.Color.GRAY)
            MessageUtil.sendMessage("  ALL", MessageUtil.Color.GRAY)
            for (category in Module.Category.values()) {
                MessageUtil.sendMessage("""  ${category.name}""", MessageUtil.Color.GRAY)
            }
        } else {
            for (category in Module.Category.values()) {
                if (category.name.equals(args[0], ignoreCase = true)) {
                    MessageUtil.sendMessage("Modules in ${args[0]}:", MessageUtil.Color.GRAY)
                    for (module in ToastClient.MODULE_MANAGER.getModulesInCategory(Module.Category.valueOf(args[0].toUpperCase()))) {
                        MessageUtil.sendMessage("  ${module.name}: ${module.description}", MessageUtil.Color.GRAY)
                    }
                    return
                }
            }
            if (args[0].equals("ALL", ignoreCase = true)) {
                sb.replace(0, sb.capacity(), "")
                sb.append("Modules (${ToastClient.MODULE_MANAGER.modules.size}): ")
                for (module in ToastClient.MODULE_MANAGER.modules) {
                    sb.append("${module.name}, ")
                }
                sb.replace(sb.lastIndexOf(", "), sb.lastIndexOf(", ") + 1, "")
                MessageUtil.sendMessage(sb.toString(), MessageUtil.Color.GRAY)
                sb.replace(0, sb.capacity(), "")
                return
            }
            MessageUtil.sendMessage("${args[0]} is not a valid category.", MessageUtil.Color.RED)
        }
    }
}