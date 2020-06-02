package toast.client.commands.cmds;

import toast.client.ToastClient;
import toast.client.commands.Command;
import toast.client.utils.Logger;

public class Prefix extends Command {
    public Prefix() {
        super("prefix [prefix]", "Rebind command prefix", false, "prefix");
    }

    @Override
    public void run(String[] args) {
        if(!(ToastClient.cmdPrefix == null)){
            ToastClient.cmdPrefix = args[0];
            Logger.message("Command prefix set to: " + ToastClient.cmdPrefix, Logger.INFO);
        }
    }
}
