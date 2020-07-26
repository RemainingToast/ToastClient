package toast.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.awt.*;

public class MessageUtil {
    /*
     * Added by RemainingToast 12/07/20
     */

    public static final String CHAT_PREFIX = Formatting.DARK_GRAY + "[" + Formatting.RED + Formatting.BOLD + "Toast" + Formatting.DARK_GRAY + "] ";
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void sendRawMessage(String message) {
        mc.inGameHud.addChatMessage(MessageType.CHAT, new LiteralText(message));
    }

    public static void sendPublicMessage(String message) {
        if(mc.player == null) return;
        mc.player.sendChatMessage(message);
    }

    public static void defaultErrorMessage(){
        sendRawMessage(CHAT_PREFIX + Formatting.RED + "Computer Says No.");
    }

    public static void sendMessage(String message, Color color) {
        switch (color) {
            case DARK_RED:
                sendRawMessage(CHAT_PREFIX + Formatting.DARK_RED + message);
                break;
            case RED:
                sendRawMessage(CHAT_PREFIX + Formatting.RED + message);
                break;
            case GOLD:
                sendRawMessage(CHAT_PREFIX + Formatting.GOLD + message);
                break;
            case YELLOW:
                sendRawMessage(CHAT_PREFIX + Formatting.YELLOW + message);
                break;
            case DARK_GREEN:
                sendRawMessage(CHAT_PREFIX + Formatting.DARK_GREEN + message);
                break;
            case GREEN:
                sendRawMessage(CHAT_PREFIX + Formatting.GREEN + message);
                break;
            case AQUA:
                sendRawMessage(CHAT_PREFIX + Formatting.AQUA + message);
                break;
            case DARK_AQUA:
                sendRawMessage(CHAT_PREFIX + Formatting.DARK_AQUA + message);
                break;
            case DARK_BLUE:
                sendRawMessage(CHAT_PREFIX + Formatting.DARK_BLUE + message);
                break;
            case BLUE:
                sendRawMessage(CHAT_PREFIX + Formatting.BLUE + message);
                break;
            case LIGHT_PURPLE:
                sendRawMessage(CHAT_PREFIX + Formatting.LIGHT_PURPLE + message);
                break;
            case DARK_PURPLE:
                sendRawMessage(CHAT_PREFIX + Formatting.DARK_PURPLE + message);
                break;
            case WHITE:
                sendRawMessage(CHAT_PREFIX + Formatting.WHITE + message);
                break;
            default: GRAY:
                sendRawMessage(CHAT_PREFIX + Formatting.GRAY + message);
                break;
            case DARK_GRAY:
                sendRawMessage(CHAT_PREFIX + Formatting.DARK_GRAY + message);
                break;
            case BLACK:
                sendRawMessage(CHAT_PREFIX + Formatting.BLACK + message);
                break;
        }
    }

    //A - Z Please
    public enum Color {
        DARK_RED,
        RED,
        GOLD,
        YELLOW,
        DARK_GREEN,
        GREEN,
        AQUA,
        DARK_AQUA,
        DARK_BLUE,
        BLUE,
        LIGHT_PURPLE,
        DARK_PURPLE,
        WHITE,
        GRAY,
        DARK_GRAY,
        BLACK
    }
}
