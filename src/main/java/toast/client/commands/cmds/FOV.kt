package toast.client.commands.cmds

import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.KeyUtil
import toast.client.utils.Logger

/**
 * Command to set the player's field of view to a specific value
 */
class FOV : Command("FOV", "${ToastClient.cmdPrefix}fov [fov]", "change fov", false, "fov") {
    override fun run(args: Array<String>) {
        if (args.isNotEmpty()) {
            if (KeyUtil.isNumeric(args[0])) {
                when {
                    args[0].toInt() >= 150 -> {
                        Logger.message("Max 150, FOV Set to 150", Logger.EMPTY, false)
                        mc.options.fov = 150.0
                    }
                    args[0].toInt() < 10 -> {
                        Logger.message("Min 10, FOV Set to 10", Logger.EMPTY, false)
                        mc.options.fov = 10.0
                    }
                    else -> {
                        mc.options.fov = args[0].toInt().toDouble()
                        Logger.message("Successfully set FOV to: ${mc.options.fov}", Logger.SUCCESS, false)
                    }
                }
            }
        }
    }
}