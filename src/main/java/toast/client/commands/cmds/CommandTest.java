package toast.client.commands.cmds;

import net.minecraft.client.MinecraftClient;
import toast.client.commands.Command;
import toast.client.utils.Logger;

public class CommandTest extends Command {

    public CommandTest() {
        super("test", "Secret test command", true, "test");
    }

    public void run(String[] args) {
        Logger.message(";OOO you found secret test command", Logger.INFO);
        MinecraftClient.getInstance().inGameHud.setOverlayMessage("hello me secret command", true);
    }
}
