package toast.client.modules;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import toast.client.modules.combat.AutoRespawn;
import toast.client.modules.combat.AutoTotem;
import toast.client.modules.combat.BowSpam;
import toast.client.modules.combat.KillAura;
import toast.client.modules.misc.*;
import toast.client.modules.movement.*;
import toast.client.modules.player.AutoTool;
import toast.client.modules.player.Surround;
import toast.client.modules.render.ClickGui;
import toast.client.modules.render.Fullbright;
import toast.client.modules.render.HUD;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static toast.client.ToastClient.CONFIG_MANAGER;

@Environment(EnvType.CLIENT)
public class ModuleManager {
    public static final CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<>();

    public void onKey(int key, int action) {
        for (Module module : modules) {
            if (module.getKey() == key && action == GLFW.GLFW_PRESS && MinecraftClient.getInstance().currentScreen == null) {
                if (MinecraftClient.getInstance().inGameHud.getChatHud().isChatFocused()) continue;
                if (!module.getClass().equals(Panic.class) && Panic.IsPanicking()) return;
                module.toggle();
            }
        }
    }

    public Module getModule(Class<? extends Module> moduleClass) {
        for (Module module : modules) {
            if (module.getClass() == moduleClass) {
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

    public void loadModules() {
        modules.clear();
        // alphabetical order please
        modules.add(new AutoWalk());
        modules.add(new AutoRespawn());
        modules.add(new AutoTool());
        modules.add(new AutoTotem());
        //modules.add(new BlockESP());
        modules.add(new BowSpam());
        modules.add(new CustomChat());
        modules.add(new ClickGui());
        modules.add(new FastStop());
        modules.add(new Flight());
        modules.add(new Fullbright());
        //modules.add(new HoleESP());
        modules.add(new HUD());
        modules.add(new KillAura());
        //modules.add(new NameTags());
        modules.add(new NoFall());
        modules.add(new Panic());
        modules.add(new PortalChat());
        //modules.add(new ShulkerPreview());
        modules.add(new Spammer());
        modules.add(new Sprint());
        //modules.add(new StorageESP());
        modules.add(new Surround());
        modules.add(new Velocity());
        CONFIG_MANAGER.enableWrite();
        CONFIG_MANAGER.loadConfig();
        CONFIG_MANAGER.loadKeyBinds();
        CONFIG_MANAGER.loadModules();
    }
}