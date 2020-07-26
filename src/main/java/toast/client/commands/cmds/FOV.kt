package toast.client.commands.cmds

import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.KeyUtil
import toast.client.utils.MessageUtil


/**
 * Command to set the player's field of view to a specific value
 */
class FOV : Command("FOV", "${ToastClient.cmdPrefix}fov [fov]", "change fov", false, "fov") {
    override fun run(args: Array<String>) {
        if (args.isNotEmpty()) {
            if (KeyUtil.isNumeric(args[0])) {
                when {
                    args[0].toInt() >= 150 -> {
                        MessageUtil.sendMessage("Max 150, FOV Set to 150", MessageUtil.Color.GREEN)
                        mc.options.fov = 150.0
                    }
                    args[0].toInt() < 10 -> {
                        MessageUtil.sendMessage("Min 10, FOV Set to 10", MessageUtil.Color.GREEN)
                        mc.options.fov = 10.0
                    }
                    else -> {
                        mc.options.fov = args[0].toInt().toDouble()
                        MessageUtil.sendMessage("Successfully set FOV to: ${mc.options.fov}", MessageUtil.Color.GREEN)
                    }
                }
            }else{
                when (args[0].toLowerCase()) {
                    "max", "m" -> {
                        mc.options.fov = 150.0
                        MessageUtil.sendMessage("Successfully set FOV to: ${mc.options.fov}", MessageUtil.Color.GREEN)
                    }
                    "normal", "mid", "medium" -> {
                        mc.options.fov = 70.0
                        MessageUtil.sendMessage("Successfully set FOV to: ${mc.options.fov}", MessageUtil.Color.GREEN)
                    }
                    "quakepro", "quake", "q" -> {
                        mc.options.fov = 110.0
                        MessageUtil.sendMessage("Successfully set FOV to: ${mc.options.fov}", MessageUtil.Color.GREEN)
                    }
                    "low", "l", "min" -> {
                        mc.options.fov = 30.0
                        MessageUtil.sendMessage("Successfully set FOV to: ${mc.options.fov}", MessageUtil.Color.GREEN)
                    }
                    else -> MessageUtil.sendMessage("Invalid arguments, enter a number between 10 and 150 inclusive or a preset: Min, Mid, Max.", MessageUtil.Color.RED)
                }
            }
        }
    }
}