package dev.toastmc.client.command.cmds

import dev.toastmc.client.ToastClient
import dev.toastmc.client.command.Command
import dev.toastmc.client.command.CommandManifest
import dev.toastmc.client.module.Module
import dev.toastmc.client.util.MessageUtil
import dev.toastmc.client.util.MessageUtil.sendMessage
import net.minecraft.util.Formatting


@CommandManifest(
        label = "Toggle",
        aliases = ["tog", "t"],
        description = "Toggle on and off modules",
        usage = "toggle <module>"
)
class Toggle : Command(){
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
                mod.toggle()
                sendMessage("Toggled ${mod.label}${if (!mod.enabled) Formatting.RED.toString() + " OFF" else Formatting.GREEN.toString() + " ON"}", MessageUtil.Color.GRAY)
                return
            }
            sendMessage("\"${argss.toLowerCase().replace(" ", "")}\" wasn't found.", MessageUtil.Color.RED)
        }
    }
}