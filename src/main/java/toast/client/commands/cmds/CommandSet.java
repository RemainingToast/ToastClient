package toast.client.commands.cmds;

import toast.client.commands.Command;
import toast.client.utils.Logger;

public class CommandSet extends Command {
    public CommandSet() {
        super("set <module> [setting] [newvalue]", "Changes module settings", false, "set", "config", "settings");
    }

    @Override
    public void run(String[] args) {
        Logger.message("Does nothing for now...", Logger.INFO);
    }
}
