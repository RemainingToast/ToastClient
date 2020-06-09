package toast.client.commands.cmds

import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.Logger

class GuiReset : Command("GuiReset", "${ToastClient.cmdPrefix}guireset", "Regenerates clickgui category positions according to current screen size", false, "guireset", "gr", "fixgui") {
    override fun run(args: Array<String>) {
        if (ToastClient.clickGuiHasOpened) {
            ToastClient.clickGui.settings.initCategoryPositions()
            ToastClient.clickGui.settings.savePositions()
            Logger.message("Re-arranged ClickGui, if you still have problems, try setting your gui scale to minimum.", Logger.INFO, true)
        } else {
            Logger.message("ClickGui hasn't been opened!", Logger.ERR, true)
        }
    }
}