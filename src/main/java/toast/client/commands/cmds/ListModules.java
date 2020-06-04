package toast.client.commands.cmds;

import toast.client.ToastClient;
import toast.client.commands.Command;
import toast.client.modules.Module;
import toast.client.utils.Logger;

import static toast.client.ToastClient.MODULE_MANAGER;

public class ListModules extends Command {
    public ListModules() {
        super("List", ToastClient.cmdPrefix + "list [category]", "Lists categories or modules in a category", false, "cat", "list", "mods");
    }

    @Override
    public void run(String[] args) {
        if (args.length == 0) {
            Logger.message("List of categories:", Logger.INFO, true);
            for (Module.Category category : Module.Category.values()) {
                Logger.message("  " + category.name(), Logger.EMPTY, true);
            }
        } else {
            for (Module.Category category : Module.Category.values()) {
                if (category.name().equalsIgnoreCase(args[0])) {
                    Logger.message("Modules in " + args[0] + ":", Logger.INFO, true);
                    for (Module module : MODULE_MANAGER.getModulesInCategory(Module.Category.valueOf(args[0].toUpperCase()))) {
                        Logger.message("  " + module.getName() + ": " + module.getDescription(), Logger.EMPTY, true);
                    }
                    return;
                }
            }
            Logger.message(args[0] + " is not a valid category.", Logger.WARN, true);
        }
    }
}
