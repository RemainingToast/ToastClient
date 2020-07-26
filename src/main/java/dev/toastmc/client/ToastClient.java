package dev.toastmc.client;

import com.google.common.eventbus.EventBus;
import dev.toastmc.client.commands.CommandManager;
import dev.toastmc.client.gui.MyMinecraftScreen;
import dev.toastmc.client.modules.ModuleManager;
import dev.toastmc.client.modules.render.ClickGui;
import dev.toastmc.client.utils.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

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
    public static CommandManager COMMAND_HANDLER = new CommandManager();
    public static ConfigManager CONFIG_MANAGER = new ConfigManager();
    public static FileManager FILE_MANAGER = new FileManager();
    public static ClickGui clickGui;
    public static Boolean clickGuiHasOpened;
    public static EventBus eventBus = new EventBus();
    private static FabricKeyBinding keyBinding;
    private static boolean isScreenOpen = false;


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

