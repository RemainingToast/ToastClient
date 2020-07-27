package dev.toastmc.client;

import dev.toastmc.client.utils.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ToastClient implements ModInitializer {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static String version = "b1.0";
    public static String cleanPrefix = "ToastClient";
    public static String chatPrefix = Formatting.RED + "[ToastClient]";
    public static String cmdPrefix = ".";
    public static List<String> devs = Collections.singletonList("MorganAnkan, RemainingToast, Qther, Fleebs, wnuke");

    private static File directory = new File(mc.runDirectory, cleanPrefix.toLowerCase());
    
    private static FabricKeyBinding keyBinding;


    @Override
    public void onInitialize() {

        RandomMOTD.addMOTDS();
        ASCII.printFancyConsoleMSG();
        TPSCalculator.calculatorInstance = new TPSCalculator();
        System.out.println("Special thanks to all contributors of this project: ");
        System.out.println(("" + devs.toString().replaceAll("[\\[\\](){}]", "")));
        System.out.println("_________________________________________________________");
    }
}

