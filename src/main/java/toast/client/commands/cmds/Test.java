package toast.client.commands.cmds;

import net.minecraft.client.MinecraftClient;
import toast.client.ToastClient;
import toast.client.commands.Command;
import toast.client.utils.Logger;

public class Test extends Command {

    public Test() {
        super("test", "Secret test command", false, "test");
    }

    public void run(String[] args) {
//        Logger.message("ClickGUIhasOpened = "+ ToastClient.clickGuiHasOpened, Logger.INFO);
        Logger.message(";O you found secret test command", Logger.INFO);
        MinecraftClient.getInstance().inGameHud.setOverlayMessage("Toast Client on Top", true);
    }
}
