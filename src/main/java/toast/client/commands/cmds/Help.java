package toast.client.commands.cmds;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import toast.client.ToastClient;
import toast.client.commands.Command;
import toast.client.modules.misc.CustomChat;
import toast.client.utils.Logger;

import static toast.client.ToastClient.COMMAND_HANDLER;

public class Help extends Command {
    StringBuilder sb = new StringBuilder();
    Integer i = 0;
    String out;
    String desc;
    String usage;

    public Help() {
        super("Help","help [command]", "Shows all commands", false, "help", "commands");
    }

    public void run(String[] args) {
        if(mc.player == null) return;
        if (args.length < 1) {
            sb.replace(0, sb.capacity(), "");
            for (Command command : COMMAND_HANDLER.getCommands()) {
                i++;

                if (COMMAND_HANDLER.isDevCancel(command)) continue;
                if(command.getName().equals("Help")) continue;
                sb.append(Formatting.GRAY).append(command.name).append(", ");
                if(COMMAND_HANDLER.getCommands().size() == i){
                    i = 0;
                    break;
                }
            }
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText(
                    ToastClient.chatPrefix + Formatting.GRAY + " Commands (" +COMMAND_HANDLER.getCommands().size()+ "): " + sb.toString()
                    ));
        } else {
            String c = args[0].replaceFirst("\\.", "");
            Command cmd = COMMAND_HANDLER.getCommand(c);
            if (cmd == null || COMMAND_HANDLER.isDevCancel(cmd)) {
                Logger.message("Cannot find command " + c, Logger.ERR, false);
                return;
            }
            StringBuilder msg = new StringBuilder();
            StringBuilder desc = new StringBuilder();
            StringBuilder usage = new StringBuilder();
            sb.replace(0, sb.capacity(), "");
            String[] desc1 = cmd.getDesc().split(" ");
            String[] usage1 = cmd.getUsage().split(" ");
            for ( String ss : desc1) {
                desc.append(" ").append(Formatting.YELLOW).append(ss);
                i++;
                System.out.println(ss);
                if(desc1.length == i) {
                    i = 0;
                    break;
                }
            }
            for ( String ss : usage1) {
                usage.append(" ").append(Formatting.YELLOW).append(ss);
                i++;
                System.out.println(ss);
                if(usage1.length == i) {
                    i = 0;
                    break;
                }
            }
            msg.append("Help for Command: " + Formatting.YELLOW + cmd.getName());
            msg.append("\n").append(Formatting.GRAY + " - Aliases: " + Formatting.YELLOW).append(ToastClient.cmdPrefix).append(String.join(Formatting.GRAY + ", " + Formatting.YELLOW + ToastClient.cmdPrefix, cmd.getAliases())).append("\n");
            msg.append(Formatting.GRAY + " - Usage:" + Formatting.YELLOW + usage.toString() + "\n");
            msg.append(Formatting.GRAY + " - Description:" + Formatting.YELLOW).append(desc.toString());
            out = msg.toString();
            Logger.message(out, Logger.EMPTY, false);
        }
    }
}
