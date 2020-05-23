package toast.client.commands;

import toast.client.ToastClient;
import toast.client.commands.cmds.*;
import toast.client.utils.Logger;
import net.minecraft.client.MinecraftClient;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommandHandler {
    public static CopyOnWriteArrayList<Command> commands = new CopyOnWriteArrayList<Command>();

    public static void executeCmd(String name, String[] args) {
        boolean notfound = true;
        for (Command command : commands) {
            for (String alias : command.aliases) {
                if(alias.equalsIgnoreCase(name)) {
                    try {
                        if (isDevCancel(command)) {
                            notfound = true;
                            continue;
                        }
                        command.run(args);
                        notfound = false;
                    } catch(Exception err) {
                        err.printStackTrace();
                        Logger.message("Sorry but something went wrong", Logger.ERR);
                    }
                }
            }
        }
        if(notfound) {
            Logger.message("Cannot find command "+ ToastClient.cmdPrefix+name, Logger.ERR);
        }
    }

    public static boolean isDevCancel(Command c) {
        return c.isDev() && !ToastClient.devs.contains(Objects.requireNonNull(MinecraftClient.getInstance().player).getDisplayName().asFormattedString());
    }

    public static CopyOnWriteArrayList<Command> getCommands() {
        return commands;
    }

    public static Command getCommand(String cmd) {
        for (Command command : commands) {
            for (String alias : command.aliases) {
                if (alias.equalsIgnoreCase(cmd)) {
                    return command;
                }
            }
        }
        return null;
    }

    public static void initCommands() {
        // alphabetical order please
        commands.add(new CommandHelp());
        commands.add(new CommandReload());
        commands.add(new CommandTest());
        commands.add(new CommandToggle());
    }
}
