package toast.client.commands.cmds;

import toast.client.ToastClient;
import toast.client.commands.Command;
import toast.client.modules.Module;

import java.util.ArrayList;
import java.util.List;

import static toast.client.ToastClient.MODULE_MANAGER;

public class Panic extends Command {

    private static boolean isPanicking = false;
    private final List<Module> wasEnabled = new ArrayList<>();

    public Panic() {
        super("Panic", ToastClient.cmdPrefix + "panic", "shutdowns client", false, "shutdown", "panic");
    }

    @Override
    public void run(String[] args) throws InterruptedException {
        if (mc.currentScreen != null) return;
        isPanicking = true;
        for (Module module : MODULE_MANAGER.getModules()) {
            if (module.isEnabled() && !module.getClass().equals(this.getClass())) {
                module.setEnabled(false);
                wasEnabled.add(module);
            }
        }
        if (mc.inGameHud != null) {
//            List<String> msgs = mc.inGameHud.getChatHud().getMessageHistory(); // doesn't work no idea why doesn't delete shit
            mc.inGameHud.getChatHud().clear(true); //perhaps try this
         /*   List<Integer> toDelete = new ArrayList<>();
            for (int i = msgs.size() - 1; i >= 0; i--) {
                if (msgs.get(i).contains(ToastClient.cleanPrefix)) {
                    toDelete.add(i);
                }
            }
            for (Integer msgid : toDelete) {
                mc.inGameHud.getChatHud().removeMessage(msgid);
            } */
            mc.updateWindowTitle();
        }
    }
}
