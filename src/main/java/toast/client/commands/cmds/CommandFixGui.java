package toast.client.commands.cmds;

import toast.client.commands.Command;
import toast.client.utils.Logger;

import static toast.client.ToastClient.clickGui;

public class CommandFixGui extends Command {
    public CommandFixGui() {
        super("fixgui", "Regenerates clickgui category positions according to current screen size", false, "fixgui", "fg");
    }

    public void run(String[] args) {
        clickGui.getSettings().initCategoryPositions();
        clickGui.getSettings().savePositions();
        Logger.message("Re-arranged ClickGui, if you still have problems, try setting your gui scale to minimum.", Logger.INFO);
    }
}