package toast.client.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import toast.client.modules.Module;
import toast.client.modules.config.Setting;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.TreeMap;

import static toast.client.ToastClient.MODULE_MANAGER;

@Environment(EnvType.CLIENT)
public class ConfigManager {
    public static final String configFile = "config.json";
    public static final String modulesFile = "modules.json";
    public static final String keybindsFile = "keybinds.json";
    public static final String macrosFile = "macros.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String disabledOnStart = "Panic, ClickGui";
    public Map<String, Integer> macros;
    private boolean canWrite = false;

    public void writeConfig() {
        if (canWrite) {
            Map<String, Map<String, Setting>> config = new TreeMap<>();
            for (Module module : MODULE_MANAGER.getModules()) {
                config.put(module.getName(), module.getSettings().getSettings());
            }
            FileManager.writeFile(configFile, gson.toJson(config));
        }
    }

    public void loadConfig() {
        Map<String, Map<String, Setting>> config;
        try {
            config = gson.fromJson(new FileReader(FileManager.createFile(configFile)), new TypeToken<Map<String, Map<String, Setting>>>() {
            }.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        if (config == null || config.isEmpty()) {
            writeConfig();
            return;
        }
        for (Module module : MODULE_MANAGER.getModules()) {
            if (config.containsKey(module.getName())) {
                for (Map.Entry<String, Setting> setting : config.get(module.getName()).entrySet()) {
                    if (module.getSettings().getSettings().containsKey(setting.getKey())) {
                        module.getSettings().getSettings().replace(setting.getKey(), setting.getValue());
                    }
                }
            }
        }
    }

    public void writeKeyBinds() {
        Map<String, Integer> keybinds;
        if (canWrite) {
            keybinds = new TreeMap<>();
            for (Module module : MODULE_MANAGER.getModules()) {
                keybinds.put(module.getName(), module.getKey());
            }
            FileManager.writeFile(keybindsFile, gson.toJson(keybinds));
        }
    }

    public void loadKeyBinds() {
        Map<String, Integer> keybinds;
        try {
            keybinds = gson.fromJson(new FileReader(FileManager.createFile(keybindsFile)), new TypeToken<Map<String, Integer>>() {
            }.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        if (keybinds == null || keybinds.isEmpty()) {
            writeKeyBinds();
            return;
        }
        for (Module module : MODULE_MANAGER.getModules()) {
            if (module != null) {
                if (keybinds.containsKey(module.getName())) {
                    module.setKey(keybinds.get(module.getName()));
                }
            }
        }
    }

    public void writeMacros(String command, int key) {
        if (canWrite) {
            macros = new TreeMap<>();
            macros.put(command, key);
            FileManager.writeFile(macrosFile, gson.toJson(macros));
        }
    }

    public void loadMacros() {
        try {
            macros = gson.fromJson(new FileReader(FileManager.createFile(macrosFile)), new TypeToken<Map<String, Integer>>() {
            }.getType());
            if (macros == null) macros = new TreeMap<>();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void checkForMacro(int key, int action) {
        if (MinecraftClient.getInstance().player != null) {
            loadMacros();
            if (action == GLFW.GLFW_PRESS) {
                for (Map.Entry<String, Integer> entry : macros.entrySet()) {
                    String command = entry.getKey();
                    Integer _key = entry.getValue();
                    if (key == _key) {
                        MinecraftClient.getInstance().player.sendChatMessage(command);
                    }
                }
            }
        }
    }

    public void writeModules() {
        if (canWrite) {
            Map<String, Boolean> modules = new TreeMap<>();
            for (Module module : MODULE_MANAGER.getModules()) {
                modules.put(module.getName(), module.isEnabled());
            }
            FileManager.writeFile(modulesFile, gson.toJson(modules));
        }
    }

    public void loadModules() {
        Map<String, Boolean> modules;
        try {
            modules = gson.fromJson(new FileReader(FileManager.createFile(modulesFile)), new TypeToken<Map<String, Boolean>>() {
            }.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        if (modules == null || modules.isEmpty()) {
            writeModules();
            return;
        }
        for (Module module : MODULE_MANAGER.getModules()) {
            if (module != null) {
                if (!disabledOnStart.contains(module.getName())) {
                    if (modules.containsKey(module.getName())) {
                        module.setEnabled(modules.get(module.getName()));
                    }
                } else {
                    module.setEnabled(false);
                }
            }
        }
    }

    public void enableWrite() {
        this.canWrite = true;
    }
}
