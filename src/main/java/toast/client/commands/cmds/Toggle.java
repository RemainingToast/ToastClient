package toast.client.commands.cmds;

import toast.client.commands.Command;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;
import toast.client.utils.Logger;

public class Toggle extends Command {
    public Toggle() {
        super("toggle [module]", "Toggles the specified module", false, "toggle", "t");
    }

    public void run(String[] args) {
        if (args.length < 1) {
            Logger.message("Please provide a module name", Logger.WARN);
        } else {
            for (Module module : ModuleManager.getModules()) {
                if (module.getName().replaceAll(" ", "").toLowerCase().equals(args[0].toLowerCase())) {
                    module.toggle();
                    if (module.isEnabled()) {
                        Logger.message("Enabled " + module.getName(), Logger.INFO);
                    } else {
                        Logger.message("Disabled " + module.getName(), Logger.INFO);
                    }
                    return;
                }
            }
            Logger.message("Could not find module " + args[0], Logger.WARN);
        }
    }
}
