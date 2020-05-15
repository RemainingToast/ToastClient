package toast.client.utils;

import toast.client.lemongui.settings.Setting;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config {
    public static List<String> modulesLines;
    public static List<String> optionsLines;
    public static List<String> configLines;

    public static void initConfigManager() {
        FileManager.createFile(new File("modules.txt"));
        FileManager.createFile(new File("options.txt"));
        FileManager.createFile(new File("config.txt"));
    }

    public static void updateRead() {
        // update lines
        modulesLines = FileManager.readFile("modules.txt");
        optionsLines = FileManager.readFile("options.txt");
        configLines = FileManager.readFile("config.txt");
    }

    public static List<String> getModulesLines() {
        return modulesLines;
    }

    public static List<String> getOptionsLines() {
        return optionsLines;
    }

    public static void writeModules() {
        String modules = "";
        for (Module module : ModuleManager.modules) {
            modules += module.getName()+":"+module.isEnabled()+"\n";
           // System.out.println(module.getName()+":"+module.isEnabled());
        }
        FileManager.writeFile("modules.txt", modules);
        updateRead();
    }

    public static void writeOptions() {
        String options = "";
        for (Module module : ModuleManager.modules) {
            options+=parseSettings(module)+"\n";
            //System.out.println(module.getName()+" -> "+parseSettings(module));
        }
        FileManager.writeFile("options.txt", options);
        updateRead();
    }

    public static void writeConfig() {
        FileManager.writeFile("config.txt", "prefix:.");// hardcoded for now
        updateRead();
    }

    public static String parseSettings(Module m) {
        String settingString = m.getName()+"|";
            ArrayList<Setting> settings = ModuleManager.setmgr.getSettingsByMod(m);
            if(settings == null) return settingString;
            for (Setting setting : settings) {
                if (setting.isCheck()) {
                    settingString += "[CHECK]" + setting.getName() + ":" + setting.getValBoolean() + "|";
                } else if (setting.isCombo()) {
                    settingString += "[COMBO]" + setting.getName() + ":" + setting.getValString() + "|";
                } else if (setting.isSlider()) {
                    settingString += "[SLIDER]" + setting.getName() + ":" + setting.getValDouble() + ":" + setting.getMin() + ":" + setting.getMax() + "|";
                }
            }
            settingString+="[KEYBIND]key:"+m.getKey()+"|";
        return settingString;
    }

    public static int getKeyBind(String settingString) {
        String[] split = settingString.split("\\|");
        int key = -1;
        for (String s : split) {
            if(s.startsWith("[KEYBIND]")) {
                try {
                    key = Integer.parseInt(s.split(":")[1]);
                } catch(Exception e) {
                    System.out.println("failed to parse keybind: "+s);
                }
            }
        }
        return key;
    }

    public static List<Setting> extractSettings(String settingString) {
        List<Setting> result = new ArrayList<>();
        String[] split = settingString.split("\\|");
        if(split.length < 1) return null;
        String mName = split[0];
        for (String s : split) {
            if (s.equals(mName) || s.equals("")) continue;
            try {
                if (s.startsWith("[CHECK]")) {
                    Setting theSetting = new Setting(s.split("\\[CHECK]")[1].split(":")[0],
                            ModuleManager.getModule(mName), Boolean.parseBoolean(s.split(":")[1]));
                    result.add(theSetting);
                } else if (s.startsWith("[COMBO]")) {
                    Setting theSetting = new Setting(s.split("\\[COMBO]")[1].split(":")[0],
                            ModuleManager.getModule(mName), s.split(":")[1], new ArrayList<>());
                    result.add(theSetting);
                } else if (s.startsWith("[SLIDER]")) {
                    Setting theSetting = new Setting(s.split("\\[SLIDER]")[1].split(":")[0],
                            ModuleManager.getModule(mName), Double.parseDouble(s.split(":")[1]), Double.parseDouble(s.split(":")[2]), Double.parseDouble(s.split(":")[3]), false);
                    result.add(theSetting);
                }
            } catch(Exception e) {
                System.out.println("failed to parse setting: "+s);
            }
        }
        return result;
    }
}
