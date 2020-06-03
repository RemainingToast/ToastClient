package toast.client.commands.cmds;

import toast.client.commands.Command;
import toast.client.utils.KeyUtil;

public class FOV extends Command {

    Integer fov;

    public FOV() {
        super("fov [fovnumber]", "change fov", false, "fov");
    }

    @Override
    public void run(String[] args) throws InterruptedException {
        if(args.length > 0){
            if(KeyUtil.isNumeric(args[0])){
                if((Integer.parseInt(args[0]) >= 150)){
                    fov = 150;
                    mc.options.fov = fov;
                }
                else if((Integer.parseInt(args[0]) < 10)){
                    fov = 10;
                    mc.options.fov = fov;
                }else { mc.options.fov = Integer.parseInt(args[0]); }
            }
        }
    }
}
