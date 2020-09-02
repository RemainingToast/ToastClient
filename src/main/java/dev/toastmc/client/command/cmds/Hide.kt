package dev.toastmc.client.command.cmds

import dev.toastmc.client.ToastClient
import dev.toastmc.client.command.Command
import dev.toastmc.client.command.CommandManifest
import dev.toastmc.client.module.Module
import dev.toastmc.client.util.MessageUtil
import net.minecraft.util.Formatting

@CommandManifest(
    label = "Hide",
    description = "Hide Modules in ArrayList",
    aliases = ["hide", "draw"]
)
class Hide : Command(){
    override fun run(args: Array<String>) {
        if (mc.player == null) return
        if (args.isEmpty()) {
            MessageUtil.defaultErrorMessage()
            return
        }
        if(args.isNotEmpty()){
            var argss = ""
            for (s in args) {
                argss += s
            }
            val mod: Module? = ToastClient.MODULE_MANAGER.getModuleByName(argss.toLowerCase().replace(" ", ""))
            if(mod != null){
                mod.setHidden(!mod.hidden)
                MessageUtil.sendMessage("${mod.label} is now being ${if (mod.hidden) Formatting.RED.toString() + "HIDDEN" else Formatting.GREEN.toString() + "SHOWN"}", MessageUtil.Color.GRAY)
                return
            }
            MessageUtil.sendMessage("\"${argss.toLowerCase().replace(" ", "")}\" wasn't found.", MessageUtil.Color.RED)
        }
    }
}