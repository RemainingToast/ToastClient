package toast.client.modules.misc;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import toast.client.ToastClient;
import toast.client.event.EventImpl;
import toast.client.event.events.network.EventPacketSent;
import toast.client.modules.Module;
import toast.client.utils.FancyChatUtil;

public class FancyChat extends Module {
    private boolean isMadeByFancyChat = false;

    public FancyChat() {
        super("FancyChat", Category.MISC, -1);
        this.settings.addMode("Mode", "Classic", "Classic", "Rainbow", "Spaces", "FaNcY", "Watermark", "Grammar");
        this.settings.addBoolean("Normal client cmds", true);
        this.settings.addBoolean("Normal mc cmds", false);
    }

    public void onEnable() {
        isMadeByFancyChat = true;
    }

    @EventImpl
    public void onEvent(EventPacketSent event) {
        if (event.getPacket() instanceof ChatMessageC2SPacket) {
            String packetMessage = ((ChatMessageC2SPacket) event.getPacket()).getChatMessage();
            if (packetMessage.startsWith(ToastClient.cmdPrefix) && this.getBool("Normal client cmds")) return;
            if (packetMessage.startsWith("/") && this.getBool("Normal mc cmds")) return;
            if (this.getMode().equals("Classic")) {
                String msg = FancyChatUtil.classicFancy(packetMessage);
                isMadeByFancyChat = !isMadeByFancyChat;
                if (isMadeByFancyChat) return;
                event.setCancelled(true);
                mc.player.sendChatMessage(msg);
            }
            if (this.getMode().equals("Rainbow")) {
                String msg = FancyChatUtil.rainbowText(packetMessage);
                isMadeByFancyChat = !isMadeByFancyChat;
                if (isMadeByFancyChat) return;
                event.setCancelled(true);
                mc.player.sendChatMessage(msg);
            }
            if (this.getMode().equals("Spaces")) {
                String msg = FancyChatUtil.spaces(packetMessage);
                isMadeByFancyChat = !isMadeByFancyChat;
                if (isMadeByFancyChat) return;
                event.setCancelled(true);
                mc.player.sendChatMessage(msg);
            }
            if (this.getMode().equals("FaNcY")) {
                String msg = FancyChatUtil.FaNcY(packetMessage);
                isMadeByFancyChat = !isMadeByFancyChat;
                if (isMadeByFancyChat) return;
                event.setCancelled(true);
                mc.player.sendChatMessage(msg);
            }
            if (this.getMode().equals("Watermark")) {
                String msg = FancyChatUtil.Watermark(packetMessage);
                isMadeByFancyChat = !isMadeByFancyChat;
                if (isMadeByFancyChat) return;
                event.setCancelled(true);
                mc.player.sendChatMessage(msg);
            }
            if (this.getMode().equals("Grammar")) {
                String msg = FancyChatUtil.Grammar(packetMessage);
                isMadeByFancyChat = !isMadeByFancyChat;
                if (isMadeByFancyChat) return;
                event.setCancelled(true);
                mc.player.sendChatMessage(msg);
            }
        }
    }
}
