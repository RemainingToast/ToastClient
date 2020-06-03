package toast.client.commands.cmds;

import toast.client.commands.Command;
import toast.client.utils.Logger;
import toast.client.utils.RandomMOTD;

public class MOTD extends Command {
    public MOTD() { super("MOTD", "Shows random MOTD", false, "motd"); }
    public void run(String[] args) {
        Logger.message(RandomMOTD.randomMOTD(), Logger.EMPTY);
    }
}
