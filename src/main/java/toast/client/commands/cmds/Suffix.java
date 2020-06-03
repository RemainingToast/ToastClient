package toast.client.commands.cmds;

import toast.client.commands.Command;
import toast.client.modules.misc.CustomChat;
import toast.client.utils.FancyChatUtil;
import toast.client.utils.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Suffix extends Command {
    public Suffix() {
        super("suffix [suffix]", "Set custom chat ending", false, "suffix", "sufx");
    }

    Pattern withinQuotes = Pattern.compile("([\"'])(?:(?=(\\\\?))\\2.)*?\\1");
    Matcher matcher;
    String out;

    @Override
    public void run(String[] args) {
        if(mc.player == null) return;
        if(args.length >= 1) {
            if (!args[0].isEmpty()) {
                StringBuilder message = new StringBuilder();
                for(int i = 0; i < args.length; i++){
                    if (i > 0) message.append(" ");
                    message.append(args[i]);
                    out = message.toString();
                }
                Logger.message("Set suffix to " + out, Logger.INFO);
                CustomChat.suffix = out;
            }
        }else { Logger.message("Specify suffix!", Logger.ERR); }
    }
}
