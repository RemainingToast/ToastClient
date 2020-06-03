package toast.client.commands.cmds;

import toast.client.commands.Command;
import toast.client.utils.KeyUtil;
import toast.client.utils.Logger;

public class FOV extends Command {

//    Integer fov;

    public FOV() {
        super("fov [fovnumber]", "change fov", false, "fov");
    }

    @Override
    public void run(String[] args) throws InterruptedException {
        if(args.length > 0){
            if(KeyUtil.isNumeric(args[0])){
                if((Integer.parseInt(args[0]) >= 150)){
                    mc.options.fov = 150;
                    Logger.message("Max 150", Logger.ERR);
                }
                else if((Integer.parseInt(args[0]) < 10)){
                    mc.options.fov = 10;
                    Logger.message("Min 10", Logger.ERR);
                }else {
                    mc.options.fov = Integer.parseInt(args[0]);
                    Logger.message("Set Fov To " + mc.options.fov, Logger.SUCC);
                }
            }
        }
    }
}
