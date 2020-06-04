package toast.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import sun.rmi.runtime.Log;
import toast.client.ToastClient;

public class Logger {
    private static int i = 0;

    public static final StringBuilder sb = new StringBuilder();
    public static final int EMPTY = 0;
    public static final int INFO = 1;
    public static final int ERR = 2;
    public static final int WARN = 3;
    public static final int SUCC = 4;

    public static void message(String text, int type, Boolean prefix) {
        String prefix2 = "";
        String msg = "";
        //EMPTY
        if (type == Logger.EMPTY) {
            sb.replace(0, sb.capacity(), "");
            String[] arr = text.split(" ");
            for ( String ss : arr) {
                sb.append(" ").append(Formatting.GRAY).append(ss);
                i++;
                System.out.println(ss);
                if(arr.length == i) break;
            }
            msg = sb.toString();
        //INFO
        } else if (type == Logger.INFO) {
            sb.replace(0, sb.capacity(), "");
            String[] arr = text.split(" ");
            for ( String ss : arr) {
                sb.append(" ").append(Formatting.YELLOW).append(ss);
                i++;
                System.out.println(ss);
                if(arr.length == i) break;
            }
            if(prefix){
                prefix2 = Formatting.YELLOW + "" + Formatting.BOLD + " INFO:";
                msg = prefix2 + sb.toString();
            }else{
                msg = sb.toString();
            }
        //ERROR
        } else if (type == Logger.ERR) {
            sb.replace(0, sb.capacity(), "");
            String[] arr = text.split(" ");
            for ( String ss : arr) {
                sb.append(" ").append(Formatting.RED).append(ss);
                i++;
                System.out.println(ss);
                if(arr.length == i) break;
            }
            if(prefix){
                prefix2 = Formatting.RED + "" + Formatting.BOLD + " ERROR:";
                msg = prefix2 + sb.toString();
            }else{
                msg = sb.toString();
            }
        //WARNING
        } else if (type == Logger.WARN) {
            sb.replace(0, sb.capacity(), "");
            String[] arr = text.split(" ");
            for ( String ss : arr) {
                sb.append(" ").append(Formatting.GOLD).append(ss);
                i++;
                System.out.println(ss);
                if(arr.length == i) break;
            }
            if(prefix){
                prefix2 = Formatting.GOLD + "" + Formatting.BOLD + " WARN:";
                msg = prefix2 + sb.toString();
            }else{
                msg = sb.toString();
            }
        //SUCCESS
        } else if (type == Logger.SUCC) {
            sb.replace(0, sb.capacity(), "");
            String[] arr = text.split(" ");
            for ( String ss : arr) {
                sb.append(Formatting.GREEN).append(" ").append(ss);
                i++;
                System.out.println(ss);
                if(arr.length == i) break;
            }
            if(prefix){
                prefix2 = Formatting.GREEN + "" + Formatting.BOLD + " SUCCESS:";
                msg = prefix2 + sb.toString();
            }else{
                msg = sb.toString();
            }
        } else return;
        try {
            System.out.println(sb);
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText(ToastClient.chatPrefix + Formatting.RESET + msg));
        } catch (Exception ignored) {
            Logger.message("Failed to Log message!", ERR, true);
        }
    }
}
