package toast.client.commands.cmds;

import toast.client.ToastClient;
import toast.client.commands.Command;
import toast.client.commands.CommandHandler;
import toast.client.utils.Logger;
import net.minecraft.util.Formatting;

public class CommandHelp extends Command {
    public CommandHelp() {
        super("help [command]", "Shows all commands", false, "help", "commands");
    }

    public void run(String[] args) {
        if(args.length < 1) {
            for (Command command : CommandHandler.getCommands()) {
                if (CommandHandler.isDevCancel(command)) return;
                Logger.message(Formatting.GRAY + ToastClient.cmdPrefix + command.getAliases()[0] + " - "+Formatting.YELLOW + command.getDesc(), Logger.EMPTY);
            }
        } else {
            String c = args[0].replaceFirst("\\.", "");
            Command cmd = CommandHandler.getCommand(c);
            if(cmd == null || CommandHandler.isDevCancel(cmd)) {
                Logger.message("Cannot find command "+c, Logger.ERR);
                return;
            }
            Logger.message(Formatting.GRAY+"Aliases: "+Formatting.YELLOW+ToastClient.cmdPrefix+String.join(Formatting.GRAY+", "+Formatting.YELLOW+ToastClient.cmdPrefix, cmd.getAliases()), Logger.INFO);
            Logger.message(Formatting.GRAY+"Usage: "+Formatting.YELLOW+ToastClient.cmdPrefix+cmd.getUsage(), Logger.INFO);
            Logger.message(Formatting.GRAY+"Description: "+Formatting.YELLOW+cmd.getDesc(), Logger.INFO);
        }
    }
}
