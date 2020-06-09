package toast.client.commands.cmds;

import toast.client.ToastClient;
import toast.client.commands.Command;
import toast.client.modules.Module;
import toast.client.utils.Logger;

import static toast.client.ToastClient.MODULE_MANAGER;

public class Toggle extends Command {
    public Toggle() {
        super("Toggle", ToastClient.cmdPrefix + "toggle [module]", "Toggles the specified module", false, "toggle", "t");
    }

    public void run(String[] args) {
        if (args.length < 1) {
            Logger.message("Please provide a module name", Logger.WARN, true);
        } else {
            for (Module module : MODULE_MANAGER.getModules()) {
                if (module.getName().replaceAll(" ", "").toLowerCase().equals(args[0].toLowerCase())) {
                    module.toggle();
                    if (module.isEnabled()) {
                        Logger.message("Enabled " + module.getName(), Logger.INFO, true);
                    } else {
                        Logger.message("Disabled " + module.getName(), Logger.INFO, true);
                    }
                    return;
                }
            }
            Logger.message("Could not find module " + args[0], Logger.WARN, true);
        }
    }
}
