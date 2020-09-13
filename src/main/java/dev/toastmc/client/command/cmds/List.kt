package dev.toastmc.client.command.cmds

import dev.toastmc.client.ToastClient.Companion.MODULE_MANAGER
import dev.toastmc.client.command.Command
import dev.toastmc.client.command.CommandManifest
import dev.toastmc.client.module.Category
import dev.toastmc.client.util.MessageUtil

@CommandManifest(
        label = "List",
        description = "List Modules",
        aliases = ["modules", "mods"]
)
class List : Command() {
    override fun run(args: Array<String>) {
        val sb = StringBuilder()
        if (args.isEmpty()) {
            MessageUtil.sendMessage("List of categories:", MessageUtil.Color.GRAY)
            MessageUtil.sendMessage("  ALL", MessageUtil.Color.GRAY)
            for (category in Category.values()) {
                if(category == Category.NONE) continue
                if(MODULE_MANAGER.getModulesInCategory(category).isNotEmpty()){
                    MessageUtil.sendMessage("""  ${category.name}""", MessageUtil.Color.GRAY)
                }
            }
        } else {
            for (category in Category.values()) {
                if (category.name.equals(args[0], ignoreCase = true)) {
                    MessageUtil.sendMessage("Modules in ${args[0]}:", MessageUtil.Color.GRAY)
                    for (module in MODULE_MANAGER.getModulesInCategory(Category.valueOf(args[0].toUpperCase()))) {
                        MessageUtil.sendMessage("  ${module.label}: ${module.description}", MessageUtil.Color.GRAY)
                    }
                    return
                }
            }
            if (args[0].equals("ALL", ignoreCase = true)) {
                sb.replace(0, sb.capacity(), "")
                sb.append("Modules (${MODULE_MANAGER.modules.size}): ")
                for (module in MODULE_MANAGER.modules) {
                    sb.append("${module.label}, ")
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