package toast.client.utils;

import toast.client.lemongui.settings.Setting;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Config {

    public static File modulesFile = null;
    public static File optionsFile = null;
    public static File configFile = null;

    public static List<String> modulesLines;
    public static List<String> optionsLines;
    public static List<String> configLines;

    public static void initConfigManager() {
        modulesFile = FileManager.createFile(new File("modules.txt"));
        optionsFile = FileManager.createFile(new File("options.txt"));
        configFile = FileManager.createFile(new File("config.txt"));
    }

    public static void updateRead() {
        modulesLines = FileManager.readFile("modules.txt");
        optionsLines = FileManager.readFile("options.txt");
        configLines = FileManager.readFile("config.txt");
    }

    public static void writeModules() {
        updateRead();
        String moduleLines = "";
        for (Module module : ModuleManager.modules) {
            moduleLines += module.getName()+":"+module.isEnabled()+"\n";
        }
        FileManager.writeFile(modulesFile, moduleLines);
        updateRead();
    }

    public static void writeOptions() {
        updateRead();
        String optionLines = "";
        for (Module module : ModuleManager.modules) {
            optionLines+=parseSettings(module)+"\n";
        }
        FileManager.writeFile(optionsFile, optionLines);
        updateRead();
    }

    public static void writeConfig() {
        updateRead();
        FileManager.writeFile(configFile, "prefix:.");// hardcoded for now
        updateRead();
    }

    public static String parseSettings(Module m) {// TODO: parse it better
        String settingString = m.getName()+"|";
        for (Module module : ModuleManager.modules) {
            ArrayList<Setting> settings = ModuleManager.setmgr.getSettingsByMod(module);
            if(settings == null) continue;
            for (Setting setting : settings) {
                if (setting.isCheck()) {
                    settingString += "[CHECK]" + setting.getName() + ":" + setting.getValBoolean() + "|";
                } else if (setting.isCombo()) {
                    settingString += "[COMBO]" + setting.getName() + ":" + setting.getValString() + "|";
                } else if (setting.isSlider()) {
                    settingString += "[SLIDER]" + setting.getName() + ":" + setting.getValDouble() + "|";
                }
            }
        }
        return settingString;
    }

    public static List<Setting> extractSettings(String settingString) {
        List<Setting> result = new ArrayList<>();
        String[] split = settingString.split("\\|");
        if(split.length < 1) return null;
        String mName = split[0];
        for (String s : split) {
            if (s.equals(mName)) continue;
            if (s.startsWith("[CHECK]")) {
                Setting theSetting = new Setting(s.split("\\[CHECK]")[1].split(":")[0],
                        ModuleManager.getModule(mName), Boolean.parseBoolean(s.split(":")[1]));
                result.add(theSetting);
            } else if (s.startsWith("[COMBO]")) {
                Setting theSetting = new Setting(s.split("\\[COMBO]")[1].split(":")[0],
                        ModuleManager.getModule(mName), s.split(":")[1], new ArrayList<>());
                result.add(theSetting);
            } else if(s.startsWith("[SLIDER]")) {
                Setting theSetting = new Setting(s.split("\\[SLIDER]")[1].split(":")[0],
                        ModuleManager.getModule(mName), Double.parseDouble(s.split(":")[1]), 0, 0, false);
                result.add(theSetting);
            }
        }
    return result;
    }

    public static boolean write() throws IOException {
        /*FileManager.createFile(new File("modules.txt"));
        File file = FileManager.getFile("modules.txt", false);
        if (file == null) return false;
        List<String> lines = Files.readAllLines(file.toPath());
        FileWriter writer = new FileWriter(file);
        writer.write("");
        for (Module module : ModuleManager.modules) {
            writer.append("Name : " + module.getName() + "\n");
            writer.append("Enabled : " + module.isEnabled() + "\n");
            if (ModuleManager.setmgr.getSettingsByMod(module).isEmpty()) return false;
            for (Setting setting : ModuleManager.setmgr.getSettingsByMod(module)) {
                try {
                    writer.append(setting.getName() + " : " + setting.getValString() + "\n");
                } catch (IOException exceptionA) {
                    try {
                        writer.append(setting.getName() + " : " + setting.getValBoolean() + "\n");
                    } catch (IOException exceptionB) {
                        try {
                            writer.append(setting.getName() + " : " + setting.getValDouble() + "\n");
                        } catch (IOException ignored) { }
                    }
                }
            }
        }*/
        return false;
    }
}
