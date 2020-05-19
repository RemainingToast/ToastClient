package toast.client.commands.cmds;

import toast.client.commands.Command;
import toast.client.lemongui.clickgui.ClickGui;
import toast.client.utils.Logger;

public class CommandGuireset extends Command {
    public CommandGuireset() {
        super("guireset", "Resets the clickgui", false, "guireset", "clickguireset");
    }

    public void run(String[] args) {
        ClickGui.reset();
        Logger.message("Clickgui has been reset!", Logger.INFO);
    }
}
