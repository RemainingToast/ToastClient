package toast.client.commands.cmds;

import net.minecraft.client.MinecraftClient;
import toast.client.ToastClient;
import toast.client.commands.Command;
import toast.client.utils.Logger;
import toast.client.utils.RandomMOTD;

public class Test extends Command {

    public Test() {
        super("Test", ToastClient.cmdPrefix + "test", "Secret test command", true, "test");
    }

    public void run(String[] args) {
//        Logger.message("ClickGUIhasOpened = "+ ToastClient.clickGuiHasOpened, Logger.INFO);
//        Logger.message(";O you found secret test command", Logger.INFO);
        Logger.message(RandomMOTD.randomMOTD(), Logger.INFO, true);
        MinecraftClient.getInstance().inGameHud.setOverlayMessage("Toast Client on Top", true);
    }
}
