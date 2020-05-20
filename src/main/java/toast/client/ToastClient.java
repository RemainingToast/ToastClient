package toast.client;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Formatting;
import toast.client.commands.CommandHandler;
import toast.client.modules.ModuleManager;
import toast.client.dontobfuscate.Config;
import toast.client.utils.FileManager;
import toast.client.utils.LoginUtil;

import java.util.Collections;
import java.util.List;

public class ToastClient implements ModInitializer {
    public static String version = "b1.0";
    public static String cleanPrefix = "ToastClient";
    public static String chatPrefix = Formatting.DARK_GRAY+"["+Formatting.LIGHT_PURPLE+"ToastClient"+Formatting.DARK_GRAY+"]";
    public static String cmdPrefix = ".";
    public static List<String> devs = Collections.singletonList("MorganAnkan, RemainingToast, Qther, Fleebs, wnuke");

    public static Boolean devMode = false;

    @Override
    public void onInitialize() {
        System.out.println(cleanPrefix+" Initialized");
        System.out.println("Special thanks to all contributors of this project "+devs);
        FileManager.initFileManager();
        ModuleManager.initModules();
        CommandHandler.initCommands();
        if(devMode) {
            boolean login = LoginUtil.loginCracked("ToastDeveloper"); // avoid generating lots of .dat files in singleplayer worlds
            if (login) {
                System.out.println("Logged in as Developer");
            } else {
                System.out.println("Failed login ;C");
            }
        }
    }

}
