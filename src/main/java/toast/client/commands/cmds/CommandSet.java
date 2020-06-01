package toast.client.commands.cmds;

import org.apache.commons.lang3.math.NumberUtils;
import toast.client.commands.Command;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;
import toast.client.modules.config.Setting;
import toast.client.modules.config.SettingDef;
import toast.client.utils.Logger;

import java.util.Map;

public class CommandSet extends Command {
    public CommandSet() {
        super("set <module> [setting] [newvalue]", "Changes module settings", false, "set", "config", "settings");
    }

    public void displaySetting(String name, Setting setting, Module module) {
        if (setting.getType() == 0) {
            StringBuilder modes = new StringBuilder();
            for (String mode : module.getSettings().getSettingDef(name).getModes()) {
                modes.append(mode).append(", ");
            }
            modes = new StringBuilder((String) modes.subSequence(0, modes.length() - 3));
            Logger.message(" Mode: " + name + ", current: " + setting.getMode() + " available: " + modes, Logger.EMPTY);
        } else if (setting.getType() == 1) {
            Logger.message(" Value: " + name + ", current: " + setting.getValue() + " minimum: " + module.getSettings().getSettingDef(name).getMinValue() + " maximum: " + module.getSettings().getSettingDef(name).getMaxValue(), Logger.EMPTY);
        } else if (setting.getType() == 2) {
            Logger.message(" Toggle: " + name + ", state: " + (setting.isEnabled() ? "enabled" : "disabled"), Logger.EMPTY);
        } else {
            Logger.message("Invalid setting", Logger.ERR);
        }
    }

    @Override
    public void run(String[] args) {
        if (args.length > 0) {
            Module module = ModuleManager.getModule(args[0]);
            if (module != null) {
                if (args.length > 1) {
                    Setting setting = module.getSettings().getSetting(args[1]);
                    if (setting != null) {
                        if (args.length == 2) {
                            displaySetting(args[1], setting, module);
                        } else {
                            SettingDef settingDef = module.getSettings().getSettingDef(args[1]);
                            if (setting.getType() == 0) {
                                if (settingDef.getModes().contains(args[2])) {
                                    setting.setMode(args[2]);
                                    Logger.message("Changed value of setting " + args[1] + " to " + args[2], Logger.INFO);
                                } else {
                                    Logger.message(args[2] + " is an invalid value for this setting.", Logger.WARN);
                                }
                            } else if (setting.getType() == 1) {
                                if (NumberUtils.isParsable(args[2])) {
                                    double newNum = Double.parseDouble(args[2]);
                                    if (newNum <= settingDef.getMaxValue()) {
                                        if (newNum >= settingDef.getMinValue()) {
                                            setting.setValue(newNum);
                                            Logger.message("Changed value of setting " + args[1] + " to " + args[2], Logger.INFO);
                                        } else {
                                            Logger.message(newNum + " is too small, the minimum value is: " + settingDef.getMinValue(), Logger.WARN);
                                        }
                                    } else {
                                        Logger.message(newNum + " is too big, the maximum value is: " + settingDef.getMaxValue(), Logger.WARN);
                                    }
                                } else {
                                    Logger.message(args[2] + " is an invalid value for this setting, please give a number.", Logger.WARN);
                                }
                            } else if (setting.getType() == 2) {
                                if (args[2].equals("true") || args[2].equals("false")) {
                                    setting.setEnabled(Boolean.parseBoolean(args[2]));
                                    Logger.message("Changed value of setting " + args[1] + " to " + args[2], Logger.INFO);
                                } else {
                                    Logger.message(args[2] + " is an invalid value for this setting, please give a boolean (true/false).", Logger.WARN);
                                }
                            } else {
                                Logger.message("Internal programming error.", Logger.WARN);
                            }
                        }
                    } else {
                        Logger.message(args[1] + " is not a setting.", Logger.WARN);
                    }
                } else {
                    Logger.message("Setting(s) for module " + module.getName() + ": ", Logger.INFO);
                    for (Map.Entry<String, Setting> settingEntry : module.getSettings().getSettings().entrySet()) {
                        displaySetting(settingEntry.getKey(), settingEntry.getValue(), module);
                    }
                }
            } else {
                Logger.message(args[0] + " is not a module.", Logger.WARN);
            }
        } else {
            Logger.message("Invalid arguments.", Logger.WARN);
        }
    }
}
