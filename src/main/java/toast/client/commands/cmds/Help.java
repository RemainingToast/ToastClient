package toast.client.commands.cmds;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import toast.client.ToastClient;
import toast.client.commands.Command;
import toast.client.utils.Logger;

import static toast.client.ToastClient.COMMAND_HANDLER;

public class Help extends Command {
    StringBuilder sb = new StringBuilder();
    String out;

    public Help() {
        super("Help", "help [command]", "Shows all commands", false, "help", "commands");
    }

    public static StringBuilder loopAppend(String[] arrayToAppend) {
        StringBuilder out = new StringBuilder();
        int i = 0;
        for (String ss : arrayToAppend) {
            out.append(" ").append(Formatting.YELLOW).append(ss);
            i++;
            if (arrayToAppend.length == i) {
                break;
            }
        }
        return out;
    }

    public void run(String[] args) {
        if (mc.player == null) return;
        if (args.length < 1) {
            sb.replace(0, sb.capacity(), "");
            int i = 0;
            for (Command command : COMMAND_HANDLER.getCommands()) {
                i++;

                if (COMMAND_HANDLER.isDevCancel(command)) continue;
                if (command.getName().equals("Help")) continue;
                sb.append(Formatting.GRAY).append(command.name).append(", ");
                if (COMMAND_HANDLER.getCommands().size() == i) {
                    break;
                }
            }
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText(ToastClient.chatPrefix + Formatting.GRAY + " Commands (" + COMMAND_HANDLER.getCommands().size() + "): " + sb.toString()));
        } else {
            String c = args[0].replaceFirst("\\.", "");
            Command cmd = COMMAND_HANDLER.getCommand(c);
            if (cmd == null || COMMAND_HANDLER.isDevCancel(cmd)) {
                Logger.message("Cannot find command " + c, Logger.ERR, false);
                return;
            }
            StringBuilder msg = new StringBuilder();
            sb.replace(0, sb.capacity(), "");
            String[] desc1 = cmd.getDesc().split(" ");
            String[] usage1 = cmd.getUsage().split(" ");
            StringBuilder desc = loopAppend(desc1);
            StringBuilder usage = loopAppend(usage1);
            msg.append("Help for Command: ").append(Formatting.YELLOW).append(cmd.getName());
            msg.append("\n").append(Formatting.GRAY).append(" - Aliases: ").append(Formatting.YELLOW).append(ToastClient.cmdPrefix).append(String.join(Formatting.GRAY + ", " + Formatting.YELLOW + ToastClient.cmdPrefix, cmd.getAliases())).append("\n");
            msg.append(Formatting.GRAY).append(" - Usage:").append(Formatting.YELLOW).append(usage.toString()).append("\n");
            msg.append(Formatting.GRAY).append(" - Description:").append(Formatting.YELLOW).append(desc.toString());
            out = msg.toString();
            Logger.message(out, Logger.EMPTY, false);
        }
    }
}
