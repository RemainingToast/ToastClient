package toast.client.commands.cmds;

import toast.client.ToastClient;
import toast.client.commands.Command;
import toast.client.utils.KeyUtil;
import toast.client.utils.Logger;

import static toast.client.ToastClient.CONFIG_MANAGER;

public class Macro extends Command {
    public Macro() {
        super("Macro", ToastClient.cmdPrefix + "macro [add/remove/list] <key> <command/message>", "Allows you to bind a message to a key", false, "macros", "macro");
    }

    public void run(String[] args) {
        if (args.length == 0) {
            Logger.message("Missing arguments!", Logger.ERR, false);
            return;
        }
        switch (args[0]) {
            case "add":
                if (args.length >= 3) {
                    if (KeyUtil.isNumeric(args[1])) {
                        try {
                            System.out.println(Integer.parseInt(args[1]));
                            CONFIG_MANAGER.writeMacros(args[2], Integer.parseInt(args[1]));
                        } catch (NumberFormatException nfe) {
                            Logger.message("Failed to add macro.", Logger.ERR, false);
                            System.out.println("Failed");
                        }
                    }
                    return;
                }
                Logger.message("Missing arguments!", Logger.ERR, false);
            case "remove":
                if (args[1] == null) {
                    Logger.message("Missing arguments!", Logger.ERR, false);
                    return;
                }
                if (KeyUtil.isNumeric(args[1])) {
                    try {
                        System.out.println(Integer.parseInt(args[1]));
                        CONFIG_MANAGER.macros.forEach((command, key) -> {
                            if (key == Integer.parseInt(args[1])) {
                                CONFIG_MANAGER.macros.remove(command, key);
                                CONFIG_MANAGER.loadMacros();
                            }
                        });

                    } catch (NumberFormatException nfe) {
                        Logger.message("Failed to remove macro.", Logger.ERR, false);
                        System.out.println("Failed");
                    }
                }
            case "list":
                final String[] message = {"Key | Message\n"};
                CONFIG_MANAGER.macros.forEach((command, key) -> message[0] += (key + " | " + command));
                Logger.message(message[0], Logger.INFO, false);
            default:
                Logger.message("Could not parse command.", Logger.ERR, false);
        }
    }

}
