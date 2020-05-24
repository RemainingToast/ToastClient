package toast.client;


import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraft.MinecraftVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.Toast;
import net.minecraft.server.MinecraftServer;

import static toast.client.ToastClient.*;

public class DiscordPresence {

    public static DiscordRichPresence presence;
    private static boolean connected;
    private static final club.minnced.discord.rpc.DiscordRPC rpc;
    private static String details;
    private static String state;
    private static DiscordRPC discordRPC;

    public static void start(){
        if(connected) return;
        connected = true;

        final DiscordEventHandlers handlers = new DiscordEventHandlers();
        DiscordPresence.rpc.Discord_Initialize(APP_ID, handlers, true, "");
        DiscordPresence.presence.startTimestamp = System.currentTimeMillis() / 1000L;

        setRpcFromSettings();

        new Thread(DiscordPresence::setRpcFromSettingsNonInt, "Discord-RPC-Callback-Handler").start();
        System.out.println("RPC STARTED");
    }

    public static void end() {
        DiscordPresence.connected = false;
        DiscordPresence.rpc.Discord_Shutdown();
    }

    private static void setRpcFromSettingsNonInt() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                DiscordPresence.rpc.Discord_RunCallbacks();
                String separator = " | ";
                if(devMode){
                    details = MinecraftClient.getInstance().getGameVersion() + separator + version;
                    state = "Name: " + MinecraftClient.getInstance().getSession().getUsername() + separator + "Dev Mode!";
                } else {
                    details = MinecraftClient.getInstance().getGameVersion() + separator + version;
                    state = "Name: " + MinecraftClient.getInstance().getSession().getUsername();
                }
                DiscordPresence.presence.details = details;
                DiscordPresence.presence.state = state;
                DiscordPresence.rpc.Discord_UpdatePresence(DiscordPresence.presence);
            }
            catch (Exception e2) { e2.printStackTrace(); }
            try { Thread.sleep(4000L); }
            catch (InterruptedException e3) { e3.printStackTrace(); }
        }
    }

    private static void setRpcFromSettings() {
        details = "Version: " + ToastClient.version;
        state = "player.getBlockPos()";
        DiscordPresence.presence.details = details;
        DiscordPresence.presence.state = state;
        DiscordPresence.rpc.Discord_UpdatePresence(DiscordPresence.presence);
    }

    static {
        rpc = club.minnced.discord.rpc.DiscordRPC.INSTANCE;
        DiscordPresence.presence = new DiscordRichPresence();
        DiscordPresence.connected = false;
    }
}
