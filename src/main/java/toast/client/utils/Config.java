package toast.client.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;
import toast.client.lemongui.clickgui.settings.Settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public static Map<String, Boolean> modules;
    public static Map<String, Settings> options;
    public static Map<String, String> config;

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void initConfigManager() {
        FileManager.createFile(new File("modules.json"));
        FileManager.createFile(new File("options.json"));
        FileManager.createFile(new File("config.json"));
    }

    public static void updateRead() {
        // update
        try {
            modules = gson.fromJson(new FileReader("toastclient/modules.json"), new TypeToken<Map<String, Boolean>>(){}.getType());
            options = gson.fromJson(new FileReader("toastclient/options.json"), new TypeToken<Map<String, Map<String, Object>>>(){}.getType());
            config = gson.fromJson(new FileReader("toastclient/config.json"), new TypeToken<Map<String, String>>(){}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void writeModules() {
        Map<String, Boolean> modules = new HashMap<>();
        for (Module module : ModuleManager.modules) {
            modules.put(module.getName(), module.isEnabled());
            // System.out.println(module.getName()+":"+module.isEnabled());
        }
        FileManager.writeFile("modules.json", gson.toJson(modules));
        updateRead();
    }

    public static void writeOptions() {
        Map<String, Map<String, Object>> options = new HashMap<>();
        for (Module module : ModuleManager.getModules()) {
            options.put(module.getName(), module.getSettings());
            //System.out.println(module.getName()+" -> "+parseSettings(module));
        }
        FileManager.writeFile("options.json", gson.toJson(options));
        updateRead();
    }

    public static void writeConfig() {
        config.put("prefix", ".");
        FileManager.writeFile("config.json", gson.toJson(config));// hardcoded for now
        updateRead();
    }

    public static void loadOptions() {
        updateRead();
        for (Module module : ModuleManager.getModules()) {
            if (options.containsKey(module.getName())) {
                module.setSettings((Map<String, Object>) options.get(module.getName()));
            }
        }
    }

    public static void loadModules() {
        updateRead();
        for (Module module : ModuleManager.getModules()) {
            if (modules.containsKey(module.getName())) {
                module.setToggled(modules.get(module.getName()));
            }
        }
    }
}
