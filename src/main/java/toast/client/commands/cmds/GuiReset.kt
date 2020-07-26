package toast.client.commands.cmds

import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.MessageUtil

/**
 * Command to regenerate ClickGui category positions according to current window resolution
 */
class GuiReset : Command("GuiReset", "${ToastClient.cmdPrefix}guireset", "Makes ClickGui fit on screen as much as possible", false, "guireset", "gr", "fixgui") {
    override fun run(args: Array<String>) {
        if (ToastClient.clickGuiHasOpened) {
            ToastClient.clickGui.settings.initCategoryPositions()
            ToastClient.clickGui.settings.savePositions()
            MessageUtil.sendMessage("Re-arranged ClickGui, if you still have problems, try setting your gui scale to minimum.", MessageUtil.Color.GREEN)
        } else {
            MessageUtil.sendMessage("ClickGui hasn't been opened!", MessageUtil.Color.RED)
        }
    }
}