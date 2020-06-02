package toast.client.modules;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.reflections.Reflections;
import toast.client.modules.misc.Panic;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static toast.client.ToastClient.CONFIG_MANAGER;

@Environment(EnvType.CLIENT)
public class ModuleManager {
    private CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<>();

    public void onKey(long window, int key, int scancode, int action, int mods) {
        if (modules == null) return;
        for (Module module : modules) {
            if (module.getKey() == key && action == GLFW.GLFW_PRESS && MinecraftClient.getInstance().currentScreen == null) {
                if (MinecraftClient.getInstance().inGameHud.getChatHud().isChatFocused()) continue;
                if (!module.getClass().equals(Panic.class) && Panic.IsPanicking()) return;
                module.toggle();
            }
        }
    }

    public Module getModule(Class classs) {
        for (Module module : modules) {
            if (module.getClass() == classs) {
                return module;
            }
        }
        return null;
    }

    public Module getModule(String name) {
        for (Module module : modules) {
            if (module.getName().equals(name)) {
                return module;
            }
        }
        return null;
    }

    public CopyOnWriteArrayList<Module> getModules() {
        return modules;
    }

    public List<Module> getModulesInCategory(Module.Category category) {
        List<Module> moduleList = new ArrayList<>();
        for (Module module : modules) {
            if (module.getCategory() == category) {
                moduleList.add(module);
            }
        }
        return moduleList;
    }

    public void loadModules() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        modules.clear();
        System.out.println(this.getClass().getCanonicalName());
        Reflections reflections = new Reflections("toast.client.modules");
        Set<Class<? extends Module>> moduleClasses = reflections.getSubTypesOf(Module.class);
        for (Class<? extends Module> moduleClass : moduleClasses) {
            Module module = moduleClass.getConstructor().newInstance();
            modules.add(module);
        }
        CONFIG_MANAGER.enableWrite();
        CONFIG_MANAGER.loadConfig();
        CONFIG_MANAGER.loadKeyBinds();
        CONFIG_MANAGER.loadModules();
    }
}