package toast.client;

import ConfigManager;
import FileManager;
import com.google.common.eventbus.EventBus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Formatting;
import toast.client.commands.CommandHandler;
import toast.client.gui.clickgui.ClickGuiScreen;
import toast.client.modules.ModuleManager;
import toast.client.utils.*;

import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ToastClient implements ModInitializer {

    public static String version = "b1.0";
    public static String cleanPrefix = "ToastClient";
    public static String chatPrefix = Formatting.RED + "[ToastClient]";
    public static String cmdPrefix = ".";
    public static List<String> devs = Collections.singletonList("MorganAnkan, RemainingToast, Qther, Fleebs, wnuke");
    public static ModuleManager MODULE_MANAGER = new ModuleManager();
    public static CommandHandler COMMAND_HANDLER = new CommandHandler();
    public static ConfigManager CONFIG_MANAGER = new ConfigManager();
    public static FileManager FILE_MANAGER = new FileManager();
    public static ClickGuiScreen clickGui;
    public static Boolean clickGuiHasOpened;
    public static EventBus eventBus = new EventBus();


    @Override
    public void onInitialize() {
        if (clickGui == null) {
            clickGuiHasOpened = false;
        }
        RandomMOTD.addMOTDS();
        ASCII.printFancyConsoleMSG();
        FILE_MANAGER.initFileManager();
        MODULE_MANAGER.loadModules();
        COMMAND_HANDLER.initCommands();
        TPSCalculator.calculatorInstance = new TPSCalculator();
        System.out.println("Special thanks to all contributors of this project: ");
        System.out.println(("" + devs.toString().replaceAll("[\\[\\](){}]", "")));
        System.out.println("_________________________________________________________");
    }
}

