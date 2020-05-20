package toast.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import toast.client.ToastClient;

public class Logger {
    public static final int EMPTY = 0;
    public static final int INFO = 1;
    public static final int ERR = 2;
    public static final int WARN = 3;

    public static void message(String text, int type) {
        String prefix2 = "";
        if (type == Logger.EMPTY) {
            prefix2 = "";
        } else if (type == Logger.INFO) {
            prefix2 = Formatting.DARK_GREEN + "" + Formatting.BOLD + "INFO:" + Formatting.GREEN;
        } else if (type == Logger.ERR) {
            prefix2 = Formatting.DARK_RED + "" + Formatting.BOLD + "ERROR:" + Formatting.RED;
        } else if (type == Logger.WARN) {
            prefix2 = Formatting.GOLD + "" + Formatting.BOLD + "WARN:" + Formatting.YELLOW;
        } else return;
        try {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText(ToastClient.chatPrefix + Formatting.RESET + " " + prefix2 + (prefix2.equals("") ? "" : " ") + text));
        } catch (Exception ignored) {
        }
    }
}
