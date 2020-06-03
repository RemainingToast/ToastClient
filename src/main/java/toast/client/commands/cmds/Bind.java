package toast.client.commands.cmds;

import org.lwjgl.glfw.GLFW;
import toast.client.commands.Command;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;
import toast.client.utils.KeyUtil;
import toast.client.utils.Logger;

import static toast.client.ToastClient.CONFIG_MANAGER;
import static toast.client.ToastClient.MODULE_MANAGER;
import static toast.client.modules.ModuleManager.modules;

public class Bind extends Command {

    public Bind(){
        super("bind [all, module] [key]", "Bind module to key", false, "bind");
    }

    @Override
    public void run(String[] args) throws InterruptedException {
        if(args.length > 0){
            Module module = MODULE_MANAGER.getModule(args[0]);
            if(args[0].equals("all")){
                if(args[1].equals("none")) {

                    for (int i = 0; true; i++) {
                        modules.get(i).key = GLFW.GLFW_KEY_UNKNOWN;
                        Logger.message(modules.get(i).name + " keybind set to NONE", Logger.SUCC);
//                        System.out.println();
                        if(i == modules.size()){
                            CONFIG_MANAGER.writeKeyBinds();
                            break;
                        }
                    }
                }
            } else if(module != null){
                if(KeyUtil.isNumeric(args[1])){
                        try
                        {
                            module.setKey(Integer.parseInt(args[1]));
                            System.out.println(Integer.parseInt(args[1]));
                            CONFIG_MANAGER.writeKeyBinds();

                        }
                        catch(NumberFormatException nfe)
                        {
                            System.out.println("Failed");
                        }
                }
            }
        }
    }
}
