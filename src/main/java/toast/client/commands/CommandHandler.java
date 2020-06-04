package toast.client.commands;

import net.minecraft.client.MinecraftClient;
import toast.client.ToastClient;
import toast.client.commands.cmds.*;
import toast.client.utils.Logger;
import toast.client.utils.RandomMOTD;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommandHandler {
    public static CopyOnWriteArrayList<Command> commands = new CopyOnWriteArrayList<>();

    public void executeCmd(String name, String[] args) {
        boolean notfound = true;
        for (Command command : commands) {
            for (String alias : command.aliases) {
                if (alias.equalsIgnoreCase(name)) {
                    try {
                        if (isDevCancel(command)) {
                            notfound = true;
                            continue;
                        }
                        notfound = false;
                        command.run(args);
                    } catch (Exception err) {
                        err.printStackTrace();
                        Logger.message("Sorry but something went wrong", Logger.ERR, true);
                    }
                }
            }
        }
        if (notfound) {
            Logger.message("Cannot find command " + ToastClient.cmdPrefix + name, Logger.ERR, true);
        }
    }

    public boolean isDevCancel(Command c) {
        return c.isDev() && !ToastClient.devs.contains(Objects.requireNonNull(MinecraftClient.getInstance().player).getDisplayName().asFormattedString());
    }

    public CopyOnWriteArrayList<Command> getCommands() {
        return commands;
    }

    public Command getCommand(String cmd) {
        for (Command command : commands) {
            for (String alias : command.aliases) {
                if (alias.equalsIgnoreCase(cmd)) {
                    return command;
                }
            }
        }
        return null;
    }

    public void initCommands() {
        commands.clear();
        // alphabetical order please
        commands.add(new Bind());
        commands.add(new ClearChat());
        commands.add(new FOV());
        commands.add(new GuiReset());
        commands.add(new Help());
        commands.add(new ListModules());
        commands.add(new Panic());
        commands.add(new Prefix());
        commands.add(new Reload());
        commands.add(new MOTD());
        commands.add(new Save());
        commands.add(new Suffix());
        commands.add(new Set());
        commands.add(new Test());
        commands.add(new Toggle());
    }
}
