package dev.toastmc.client.command.cmds

import dev.toastmc.client.ToastClient
import dev.toastmc.client.command.Command
import dev.toastmc.client.command.CommandManifest
import dev.toastmc.client.module.Module
import dev.toastmc.client.util.MessageUtil

@CommandManifest(
    label = "Set",
    description = "Set module setting.",
    aliases = []
)
class Set : Command() {
    //TODO(".set flight speed 60") // .set <module> <setting> <value>
    override fun run(args: Array<String>) {
        if(mc.player == null) return
        if (args.isEmpty()) {
            MessageUtil.defaultErrorMessage()
            return
        }
        if(args.isNotEmpty()){
            val mod: Module? = ToastClient.MODULE_MANAGER.getModuleByName(args[0].toLowerCase())
            if(mod != null && args[1].isNotEmpty()){
                return
            }
            MessageUtil.sendMessage("\"${args[0]}\" wasn't found.", MessageUtil.Color.RED)
        }
    }
}