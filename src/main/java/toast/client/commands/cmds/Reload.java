package toast.client.commands.cmds;

import net.minecraft.util.Formatting;
import toast.client.commands.Command;
import toast.client.utils.Config;
import toast.client.utils.Logger;

import static toast.client.ToastClient.clickGui;
import static toast.client.ToastClient.clickGuiHasOpened;

public class Reload extends Command {
    public Reload() {
        super("reload [config]", "Reloads all or one of the configuration files", false, "reload", "rl");
    }

    public void run(String[] args) {
        if (args.length < 1) {
            Config.loadConfigAuto();
            Config.loadModulesAuto();
            Config.loadKeyBindsAuto();
            if(clickGuiHasOpened){clickGui.reloadConfig();}
            Logger.message("Reloaded all configuration files.", Logger.INFO);
        } else {
            switch (args[0]) {
                case "config":
                    Config.loadConfigAuto();
                    Logger.message("Reloaded module options.", Logger.INFO);
                    break;
                case "modules":
                    Config.loadModulesAuto();
                    Logger.message("Reloaded modules.", Logger.INFO);
                    break;
                case "keybinds":
                    Config.loadKeyBindsAuto();
                    Logger.message("Reloaded keybinds.", Logger.INFO);
                    break;
                case "clickgui":
                    if(clickGuiHasOpened){clickGui.reloadConfig();}
                    else{Logger.message("ClickGUI hasn't been opened yet", Logger.ERR);}
                    break;
                default:
                    Logger.message("Invalid argument, valid arguments are:", Logger.WARN);
                    Logger.message(Formatting.GRAY + "  modules " + Formatting.YELLOW + "reloads the enabled state of modules", Logger.EMPTY);
                    Logger.message(Formatting.GRAY + "  keybinds " + Formatting.YELLOW + "reloads all keybinds", Logger.EMPTY);
                    Logger.message(Formatting.GRAY + "  clickgui " + Formatting.YELLOW + "reloads the clickgui", Logger.EMPTY);
                    Logger.message(Formatting.GRAY + "  config " + Formatting.YELLOW + "reloads client config (not modules)", Logger.EMPTY);
            }
        }
    }
}
