package toast.client.commands.cmds;

import toast.client.commands.Command;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;
import toast.client.utils.Logger;

public class CommandListModules extends Command {
    public CommandListModules() {
        super("list [category]", "Lists categories or modules in a category", false, "cat", "list", "mods");
    }

    @Override
    public void run(String[] args) {
        if (args.length == 0) {
            Logger.message("List of categories:", Logger.INFO);
            for (Module.Category category : Module.Category.values()) {
                Logger.message("  " + category.name(), Logger.EMPTY);
            }
        } else {
            if (Module.Category.valueOf(args[0]) != null) {
                Logger.message("Modules in " + args[0] + ":", Logger.INFO);
                for (Module module : ModuleManager.getModulesInCategory(Module.Category.valueOf(args[0]))) {
                    Logger.message("  " + module.getName() + ": " + module.getDescription(), Logger.EMPTY);
                }
            } else {
                Logger.message(args[0] + " is not a valid category.", Logger.WARN);
            }
        }
    }
}
