package toast.client.commands.cmds;

import org.lwjgl.glfw.GLFW;
import toast.client.ToastClient;
import toast.client.commands.Command;
import toast.client.utils.KeyUtil;
import toast.client.utils.Logger;

import java.util.ArrayList;

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
                            CONFIG_MANAGER.loadMacros();
                            CONFIG_MANAGER.macros.put(args[2], Integer.parseInt(args[1]));
                            CONFIG_MANAGER.writeMacros();
                            Logger.message("Added macro: " + args[1] + " | " + args[2], Logger.INFO, false);
                        } catch (NumberFormatException nfe) {
                            Logger.message("Failed to add macro.", Logger.ERR, false);
                        }
                    }
                    return;
                }
                Logger.message("Missing arguments!", Logger.ERR, false);
                break;
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
                                CONFIG_MANAGER.loadMacros();
                                CONFIG_MANAGER.macros.remove(command, key);
                                CONFIG_MANAGER.writeMacros();
                                Logger.message("Removed macro: " + key + " | " + command, Logger.INFO, false);
                            }
                        });

                    } catch (NumberFormatException nfe) {
                        Logger.message("Failed to remove macro.", Logger.ERR, false);
                    }
                }
                break;
            case "list":
                final ArrayList<String> messages = new ArrayList<>();
                messages.add("KeyNum | KeyName | Message");
                CONFIG_MANAGER.macros.forEach((command, key) -> messages.add(key + " | " + GLFW.glfwGetKeyName(key, GLFW.glfwGetKeyScancode(key)) + " | " + command));
                for (String message : messages) {
                    Logger.message(message, Logger.INFO, false);
                }
                break;
            default:
                Logger.message("Could not parse command.", Logger.ERR, false);
        }
    }

}
