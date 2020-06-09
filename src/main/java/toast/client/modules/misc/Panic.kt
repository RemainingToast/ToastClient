package toast.client.modules.misc;

import org.lwjgl.glfw.GLFW;
import toast.client.ToastClient;
import toast.client.modules.Module;

import java.util.ArrayList;
import java.util.List;

import static toast.client.ToastClient.MODULE_MANAGER;

public class Panic extends Module {
    private static boolean isPanicking = false;
    private final List<Module> wasEnabled = new ArrayList<>();

    public Panic() {
        super("Panic", "Makes the client disappear until you relaunch the game.", Category.MISC, GLFW.GLFW_KEY_P);
    }

    public static boolean IsPanicking() {
        return isPanicking;
    }

    public void onEnable() {
        if (mc.currentScreen != null) return;
        isPanicking = true;
        for (Module module : MODULE_MANAGER.getModules()) {
            if (module.isEnabled() && !module.getClass().equals(this.getClass())) {
                module.setEnabled(false);
                wasEnabled.add(module);
            }
        }
        if (mc.inGameHud != null) {
            List<String> msgs = mc.inGameHud.getChatHud().getMessageHistory(); // doesn't work no idea why doesn't delete shit
            List<Integer> toDelete = new ArrayList<>();
            for (int i = msgs.size() - 1; i >= 0; i--) {
                if (msgs.get(i).contains(ToastClient.cleanPrefix)) {
                    toDelete.add(i);
                }
            }
            for (Integer msgid : toDelete) {
                mc.inGameHud.getChatHud().removeMessage(msgid);
            }
            mc.updateWindowTitle();
        }
    }

    public void onDisable() {
        isPanicking = false;
        for (Module module : MODULE_MANAGER.getModules()) {
            if (wasEnabled.contains(module)) {
                module.setEnabled(true);
                wasEnabled.remove(module);
            }
        }
        if (mc.currentScreen != null) {
            mc.updateWindowTitle();
        }
    }
}
