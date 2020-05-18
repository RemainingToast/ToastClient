package toast.client.modules;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.reflections.Reflections;
import toast.client.modules.dev.Panic;
import toast.client.utils.Config;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager {
    public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<>();
    public static void initModules() {
        try {
            loadModules();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

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

    public static CopyOnWriteArrayList<Module> getModules() {
        return modules;
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
        modules.clear();
        Reflections reflections = new Reflections("toast.client.modules");
        Set<Class<? extends Module>> moduleClasses = reflections.getSubTypesOf(Module.class);
        for (Class<? extends Module> moduleClass : moduleClasses) {
            Module module = moduleClass.getConstructor().newInstance();
            if (module.getName().equals("ClickGUI")) {
                System.out.println(module.getBool("Rainbow"));
            }
            modules.add(module);
        }
        Config.loadModules();
       // Config.loadOptions();
        Config.writeModules();
        Config.writeOptions();
    }
}

