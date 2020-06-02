package toast.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Formatting;
import toast.client.commands.CommandHandler;
import toast.client.gui.clickgui.ClickGuiScreen;
import toast.client.modules.ModuleManager;
import toast.client.utils.ASCII;
import toast.client.utils.FileManager;
import toast.client.utils.LoginUtil;
import toast.client.utils.TPSCalculator;

import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ToastClient implements ModInitializer {

    public static String version = "b1.0";
    public static String cleanPrefix = "ToastClient";
    public static String chatPrefix = Formatting.DARK_GRAY + "[" + Formatting.LIGHT_PURPLE + "ToastClient" + Formatting.DARK_GRAY + "]";
    public static String cmdPrefix = ".";
    public static List<String> devs = Collections.singletonList("MorganAnkan, RemainingToast, Qther, Fleebs, wnuke");
    public static ClickGuiScreen clickGui;
    public static Boolean clickGuiHasOpened;

    public static Boolean devMode = true;

    @Override
    public void onInitialize() {
        if(clickGui == null){
            clickGuiHasOpened = false;
        }
        ASCII.printFancyConsoleMSG();
        System.out.println(cleanPrefix + " Initialized");
        System.out.println("Special thanks to all contributors of this project " + devs);
        FileManager.initFileManager();
        ModuleManager.initModules();
        CommandHandler.initCommands();
        TPSCalculator.calculatorInstance = new TPSCalculator();
        if (devMode) {
            boolean login = LoginUtil.loginCracked("ToastDeveloper"); // avoid generating lots of .dat files in worlds
            if (login) {
                System.out.println("Logged in as Developer");
            } else {
                System.out.println("Developer login failed.");
            }
        }
    }
}
