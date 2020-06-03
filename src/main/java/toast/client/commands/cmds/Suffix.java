package toast.client.commands.cmds;

import toast.client.commands.Command;
import toast.client.modules.misc.CustomChat;
import toast.client.utils.FancyChatUtil;
import toast.client.utils.Logger;

public class Suffix extends Command {
    public Suffix() {
        super("suffix [suffix]", "Set custom chat ending", false, "suffix", "sufx");
    }

    @Override
    public void run(String[] args) {
        if(mc.player == null) return;
        if(args.length >= 1) {
            if (!args[0].isEmpty()) {
                CustomChat.suffix = args[0];
                Logger.message("Set suffix to " + args[0], Logger.INFO);
            }
        }else { Logger.message("Specify suffix!", Logger.ERR); }
    }
}
