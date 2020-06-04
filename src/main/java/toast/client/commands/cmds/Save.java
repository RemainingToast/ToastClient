package toast.client.commands.cmds;

import net.minecraft.util.Formatting;
import toast.client.ToastClient;
import toast.client.commands.Command;
import toast.client.utils.Logger;

import static toast.client.ToastClient.CONFIG_MANAGER;

public class Save extends Command {
    public Save() {
        super("Save", ToastClient.cmdPrefix + "save [config]", "Saves the configuration files", false, "save");
    }

    @Override
    public void run(String[] args) {
        if (args.length < 1) {
            CONFIG_MANAGER.writeConfig();
            CONFIG_MANAGER.writeKeyBinds();
            CONFIG_MANAGER.writeModules();
            Logger.message("Saved all configuration files.", Logger.INFO, true);
        } else {
            switch (args[0]) {
                case "config":
                    CONFIG_MANAGER.writeConfig();
                    Logger.message("Saved client config (not modules).", Logger.INFO, true);
                    break;
                case "modules":
                    CONFIG_MANAGER.writeModules();
                    Logger.message("Saved modules.", Logger.INFO, true);
                    break;
                case "keybinds":
                    CONFIG_MANAGER.writeKeyBinds();
                    Logger.message("Saved keybinds.", Logger.INFO, true);
                    break;
                default:
                    Logger.message("Invalid argument, valid arguments are:", Logger.WARN, true);
                    Logger.message(Formatting.GRAY + "  modules " + Formatting.YELLOW + "saves the enabled state of modules", Logger.EMPTY,true);
                    Logger.message(Formatting.GRAY + "  keybinds " + Formatting.YELLOW + "saves all keybinds", Logger.EMPTY,true);
                    Logger.message(Formatting.GRAY + "  config " + Formatting.YELLOW + "saves client config (not modules)", Logger.EMPTY,true);
            }
        }
    }
}
