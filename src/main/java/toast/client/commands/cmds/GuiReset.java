package toast.client.commands.cmds;

import toast.client.ToastClient;
import toast.client.commands.Command;
import toast.client.utils.Logger;

import static toast.client.ToastClient.clickGui;
import static toast.client.ToastClient.clickGuiHasOpened;

public class GuiReset extends Command {
    public GuiReset() {
        super("GuiReset", ToastClient.cmdPrefix + "guireset", "Regenerates clickgui category positions according to current screen size", false, "guireset", "gr");
    }

    public void run(String[] args) {
        if(clickGuiHasOpened) {
            clickGui.getSettings().initCategoryPositions();
            clickGui.getSettings().savePositions();
            Logger.message("Re-arranged ClickGui, if you still have problems, try setting your gui scale to minimum.", Logger.INFO, true);
        }else{
            Logger.message("ClickGui hasn't been opened!", Logger.ERR, true);
        }
    }
}