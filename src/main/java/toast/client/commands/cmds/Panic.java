package toast.client.commands.cmds;

import toast.client.ToastClient;
import toast.client.commands.Command;
import toast.client.modules.Module;

import java.util.ArrayList;
import java.util.List;

import static toast.client.ToastClient.MODULE_MANAGER;

public class Panic extends Command {

    private final List<Module> wasEnabled = new ArrayList<>();

    public Panic() {
        super("Panic", ToastClient.cmdPrefix + "panic", "shutdowns client", false, "shutdown", "panic");
    }

    @Override
    public void run(String[] args) throws InterruptedException {
        if (mc.currentScreen != null) return;
        for (Module module : MODULE_MANAGER.getModules()) {
            if (module.isEnabled()) {
                module.setEnabled(false);
                wasEnabled.add(module);
            }
        }
        if (mc.inGameHud != null) {
            mc.inGameHud.getChatHud().clear(true);
            mc.updateWindowTitle();
        }
    }
}
