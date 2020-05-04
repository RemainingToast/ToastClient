package toast.client;

import toast.client.commands.CommandHandler;
import toast.client.modules.ModuleManager;
import toast.client.utils.FileManager;
import toast.client.utils.LoginUtil;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Formatting;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ToastClient implements ModInitializer {
    public static String version = "b1.0";
    public static String cleanPrefix = "ToastClient";
    public static String chatPrefix = Formatting.DARK_GRAY+"["+Formatting.LIGHT_PURPLE+"ToastClient"+Formatting.DARK_GRAY+"]";
    public static String cmdPrefix = ".";
    public static List<String> devs = Arrays.asList("MorganAnkan", "RemainingToast", "RemainingToasted", "Yearr", "iBuyMountainDew", "Fleebs");

    @Override
    public void onInitialize() {
        System.out.println(cleanPrefix + " Initialized");
        System.out.println("Special thanks to all contributors of this project:");
        for (String dev : devs) {
            System.out.println(dev);
        }
        ModuleManager.initModules();
        CommandHandler.initCommands();
        FileManager.initFileManager();
        this.customFileInit();
        // \/ doesnt work maybe remove if u want
        /*System.out.println("before connected to: "+MinecraftClient.getInstance().getNetworkProxy().address());
        boolean connectedToProxy = ProxyUtil.connectProxy("138.68.60.8",3128, Proxy.Type.HTTP);
        if(connectedToProxy) {
            System.out.println("Connected to: "+MinecraftClient.getInstance().getNetworkProxy().address());
        } else {
            System.out.println("Failed to connect to proxy");
        }*/
        boolean login = LoginUtil.loginCracked("Toast Client On Top!"); //if u dont want to fill ur singleplayer world with 13519519375 .dat
        if(login) {
            System.out.println("Logged in as MorganAnkan");
        } else {
            System.out.println("Failed login ;C");
        }
    }

    private void customFileInit() {//TODO: actually parse the settings to the files
        FileManager.createFile(new File("modules.txt"));
        FileManager.createFile(new File("config.txt"));
    }

}