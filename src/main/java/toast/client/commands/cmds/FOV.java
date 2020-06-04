package toast.client.commands.cmds;

import toast.client.ToastClient;
import toast.client.commands.Command;
import toast.client.utils.KeyUtil;
import toast.client.utils.Logger;

public class FOV extends Command {

//    Integer fov;

    public FOV() {
        super("FOV", ToastClient.cmdPrefix + "fov [fov]", "change fov", false, "fov");
    }

    @Override
    public void run(String[] args) throws InterruptedException {
        if(args.length > 0){
            if(KeyUtil.isNumeric(args[0])){
                if((Integer.parseInt(args[0]) >= 150)){
                    Logger.message("Max 150, FOV Set to 150", Logger.EMPTY, false);
                    mc.options.fov = 150;
                }
                else if((Integer.parseInt(args[0]) < 10)){
                    Logger.message("Min 10, FOV Set to 10", Logger.EMPTY, false);
                    mc.options.fov = 10;
                }else {
                    mc.options.fov = Integer.parseInt(args[0]);
                    Logger.message("Successfully set FOV to: " + mc.options.fov, Logger.SUCC, false);
                }
            }
        }
    }
}
