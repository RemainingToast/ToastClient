
package toast.client.modules;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import toast.client.modules.combat.AutoRespawn;
import toast.client.modules.combat.BowSpam;
import toast.client.modules.combat.KillAura;
import toast.client.modules.dev.Panic;
import toast.client.modules.misc.FancyChat;
import toast.client.modules.misc.PortalChat;
import toast.client.modules.misc.Spammer;
import toast.client.modules.movement.FastStop;
import toast.client.modules.movement.Fly;
import toast.client.modules.movement.Velocity;
import toast.client.modules.player.AutoTool;
import toast.client.modules.player.Surround;
import toast.client.modules.render.ClickGui;
import toast.client.modules.render.Fullbright;
import toast.client.modules.render.HUD;
import toast.client.utils.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Environment(EnvType.CLIENT)
public class ModuleManager {
    public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<>();
    public static void initModules() {
        loadModules();
    }

    public static void onKey(long window, int key, int scancode, int action, int mods) {
        if (modules == null) return;
        for (Module module : modules) {
            if (module.getKey() == key && action == GLFW.GLFW_PRESS) {
                if(MinecraftClient.getInstance().inGameHud.getChatHud().isChatFocused()) continue;
                if(!module.getClass().equals(Panic.class) && Panic.IsPanicking()) return;
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

    private static void loadModules() {
        modules.clear();
        modules.add(new AutoRespawn());
        modules.add(new AutoTool());
        modules.add(new ClickGui());
        modules.add(new FancyChat());
        modules.add(new Fly());
        modules.add(new Fullbright());
        //modules.add(new HoleESP());
        modules.add(new HUD());
        modules.add(new KillAura());
        modules.add(new BowSpam());
        modules.add(new Panic());
        modules.add(new PortalChat());
        modules.add(new Spammer());
        modules.add(new Surround());
        modules.add(new Velocity());
        modules.add(new FastStop());
        Config.loadConfigAuto();
        Config.loadKeyBindsAuto();
        Config.loadModulesAuto();
        Config.writeConfig();
        Config.writeModules();
        Config.writeKeyBinds();
    }
}