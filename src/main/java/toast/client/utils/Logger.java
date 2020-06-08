package toast.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import toast.client.ToastClient;

public class Logger {
    public static final StringBuilder sb = new StringBuilder();
    public static final int EMPTY = 0;
    public static final int INFO = 1;
    public static final int ERR = 2;
    public static final int WARN = 3;
    public static final int SUCCESS = 4;
    private static final String INFO_PREFIX = Formatting.YELLOW + "" + Formatting.BOLD + " INFO:";
    private static final String ERR_PREFIX = Formatting.RED + "" + Formatting.BOLD + " ERROR:";
    private static final String WARN_PREFIX = Formatting.GOLD + "" + Formatting.BOLD + " WARN:";
    private static final String SUCCESS_PREFIX = Formatting.GREEN + "" + Formatting.BOLD + " SUCCESS:";
    private static int i = 0;

    public static void format(String string, Formatting format) {
        sb.replace(0, sb.capacity(), "");
        String[] arr = string.split(" ");
        for (String ss : arr) {
            sb.append(" ").append(format).append(ss);
            i++;
            if (arr.length == i) break;
        }
    }

    public static void message(String text, int type, Boolean prefix) {
        String prefixString = "";
        if (type == Logger.EMPTY) {
            prefixString = "";
            sb.replace(0, sb.capacity(), text);
        } else if (type == Logger.INFO) {
            format(text, Formatting.YELLOW);
            if (prefix) prefixString = INFO_PREFIX;
        } else if (type == Logger.ERR) {
            format(text, Formatting.RED);
            if (prefix) prefixString = ERR_PREFIX;
        } else if (type == Logger.WARN) {
            format(text, Formatting.GOLD);
            if (prefix) prefixString = WARN_PREFIX;
        } else if (type == Logger.SUCCESS) {
            format(text, Formatting.GREEN);
            if (prefix) prefixString = SUCCESS_PREFIX;
        } else return;
        try {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText(ToastClient.chatPrefix + Formatting.RESET + prefixString + sb.toString()));
        } catch (Exception ignored) {
            System.out.println("Failed send message in-game...");
        }
    }
}
