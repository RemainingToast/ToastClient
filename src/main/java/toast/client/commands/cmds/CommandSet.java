package toast.client.commands.cmds;

import toast.client.commands.Command;
import toast.client.dontobfuscate.Setting;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;
import toast.client.utils.Logger;

import java.util.Map;

public class CommandSet extends Command {
    public CommandSet() {
        super("set <module> [setting] [newvalue]", "Changes module settings", false, "set", "config", "settings");
    }

    public void displaySetting(String name, Setting setting, Module module) {
        if (setting.getType() == 0) {
            String modes = "";
            for (String mode : module.getSettings().getSettingDef(name).getModes()) {
                modes += mode + ", ";
            }
            modes.subSequence(0, modes.length() - 3);
            Logger.message(" Mode: " + name + " current: " + setting.getMode() + " available: " + modes, Logger.EMPTY);
        } else if (setting.getType() == 1) {
            Logger.message(" Value: " + name + " current: " + setting.getValue() + " minimum: " + module.getSettings().getSettingDef(name).getMinValue() + " maximum: " + module.getSettings().getSettingDef(name).getMaxValue(), Logger.EMPTY);
        } else if (setting.getType() == 2) {
            Logger.message(" Toggle " + name + " state: " + (setting.isEnabled() ? "enabled" : "disabled"), Logger.EMPTY);
        } else {
            Logger.message("Invalid setting", Logger.ERR);
        }
    }

    @Override
    public void run(String[] args) {
        if (args.length > 0) {
            if (args.length == 1) {
                Module module = ModuleManager.getModule(args[0]);
                if (module != null) {
                    Logger.message("Setting(s) for module " + module.getName() + ": ", Logger.INFO);
                    for (Map.Entry<String, Setting> settingEntry : module.getSettings().getSettings().entrySet()) {
                        displaySetting(settingEntry.getKey(), settingEntry.getValue(), module);
                    }
                } else {
                    Logger.message(args[0] + " is not a module.", Logger.WARN);
                }
            } else if (args.length == 2) {
                Module module = ModuleManager.getModule(args[0]);
                if (module != null) {
                    Setting setting = module.getSettings().getSetting(args[1]);
                    if (setting != null) {
                        displaySetting(args[1], setting, module);
                    } else {
                        Logger.message(args[1] + " is not a setting.", Logger.WARN);
                    }
                } else {
                    Logger.message(args[0] + " is not a module.", Logger.WARN);
                }
            }
        } else {
            Logger.message("Invalid arguments.", Logger.WARN);
        }
    }
}
