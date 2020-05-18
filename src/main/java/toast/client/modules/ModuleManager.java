package toast.client.modules;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import toast.client.lemongui.settings.Setting;
import toast.client.lemongui.settings.SettingsManager;
import toast.client.modules.combat.*;
import toast.client.modules.dev.*;
import toast.client.modules.misc.*;
import toast.client.modules.movement.*;
import toast.client.modules.player.*;
import toast.client.modules.render.*;
import toast.client.utils.Config;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.reflections.Reflections;

public class ModuleManager {
    public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<>();
    public static void initModules() {
        try {
            loadModules();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }
    public static SettingsManager setmgr = new SettingsManager();

    public static void onKey(long window, int key, int scancode, int action, int mods) {
        if (modules == null) return;
        for (Module module : modules) {
            if (module.getKey() == key && action == GLFW.GLFW_PRESS) {
                if(MinecraftClient.getInstance().inGameHud.getChatHud().isChatFocused()) continue;
                if(!module.getClass().equals(Panic.class) && Panic.IsPanicing()) return;
                module.toggle();
            }
        }
    }

    public static Module getModule(Class classs) {
        for (Module module : modules) {
            if(module.getClass() == classs) {
                return module;
            }
        }
        return null;
    }

    public static Module getModule(String name) {
        for (Module module : modules) {
            if(module.getName().equals(name)) {
                return module;
            }
        }
        return null;
    }

    public static List<Module> getModulesInCategory(Module.Category category) {
        List<Module> moduleList = new ArrayList<>();
        for (Module module : modules) {
            if(module.getCategory() == category) {
                moduleList.add(module);
            }
        }
        return moduleList;
    }

    private static void loadModules() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Config.updateRead();
        List<String> optionLines = Config.getOptionsLines();
        modules.clear();
        Reflections reflections = new Reflections("toast.client.modules");
        Set<Class<? extends Module>> moduleClasses = reflections.getSubTypesOf(Module.class);
        for (Class<? extends Module> moduleClass : moduleClasses) {
            Module module = moduleClass.getConstructor().newInstance();
            modules.add(module);
        }
        if(!(optionLines.size() < 1)) {
            ArrayList<Setting> totalSettings = new ArrayList<>();
            for (String line : optionLines) {
                if (line.equals("")) continue;
                List<Setting> settings = Config.extractSettings(line);
                if (settings != null) {
                    totalSettings.addAll(settings);
                }
            }
            setmgr.setSettings(totalSettings);
            Config.writeOptions();
        }
        for (String line : Config.getModulesLines()) {
            if(line.equals("") || !line.contains(":")) continue;
            Module m = getModule(line.split(":")[0]);
            if(m != null) {
                m.setToggled(Boolean.parseBoolean(line.split(":")[1]));
            }
        }
        Config.writeModules();
    }
}

