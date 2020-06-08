package toast.client.modules.misc;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import toast.client.ToastClient;
import toast.client.event.EventImpl;
import toast.client.event.events.network.EventPacketSent;
import toast.client.modules.Module;
import toast.client.utils.FancyChatUtil;

public class CustomChat extends Module {
    public static String suffix;
    public Boolean isMadeByCustomChat = false;

    public CustomChat() {
        super("CustomChat", "Custom chat messages", Category.MISC, -1);
        this.settings.addBoolean("Fancy Chat", false);
        this.settings.addBoolean("Commands", false);
        this.settings.addBoolean("Custom Suffix", false);
        this.settings.addMode("Separator", "None", "None", "Default", "Brackets");
        this.settings.addMode("Fancy chat type", "Retard", "Classic", "Retard", "Grammar", "Spaced");
    }

    @Override
    public void onEnable() {
        isMadeByCustomChat = true;
    }

    @EventImpl
    public void onEvent(EventPacketSent e) {
        if (mc.player == null) return;
        if (e.getPacket() instanceof ChatMessageC2SPacket) {
            if (!getBool("Custom Suffix")) {
                suffix = "ᴛᴏᴀѕᴛᴄʟɪᴇɴᴛ";
            }
            String packetMessage = ((ChatMessageC2SPacket) e.getPacket()).getChatMessage();
            if (packetMessage.startsWith(ToastClient.cmdPrefix) && !this.getBool("Commands")) return;
            if (packetMessage.startsWith("/") && !this.getBool("Commands")) return;
            if (!getBool("Fancy Chat")) {
                if (this.settings.getMode("Separator").equals("None")) {
                    String msg = FancyChatUtil.customSuffix(packetMessage, " ", suffix, "", false);
                    isMadeByCustomChat = !isMadeByCustomChat;
                    if (isMadeByCustomChat) return;
                    e.setCancelled(true);
                    mc.player.sendChatMessage(msg);
                }
                if (this.settings.getMode("Separator").equals("Default")) {
                    String msg = FancyChatUtil.customSuffix(packetMessage, " | ", suffix, "", false);
                    isMadeByCustomChat = !isMadeByCustomChat;
                    if (isMadeByCustomChat) return;
                    e.setCancelled(true);
                    mc.player.sendChatMessage(msg);
                }
                if (this.settings.getMode("Separator").equals("Brackets")) {
                    String msg = FancyChatUtil.customSuffix(packetMessage, " < ", suffix, " > ", true);
                    isMadeByCustomChat = !isMadeByCustomChat;
                    if (isMadeByCustomChat) return;
                    e.setCancelled(true);
                    mc.player.sendChatMessage(msg);
                }
            }
            if (getBool("Fancy Chat")) {
                if (this.settings.getMode("Fancy chat type").equals("Classic")) {
                    String msg = FancyChatUtil.classicFancy(packetMessage);
                    isMadeByCustomChat = !isMadeByCustomChat;
                    if (isMadeByCustomChat) return;
                    e.setCancelled(true);
//                    mc.player.sendChatMessage("msg1" +msg);
                    if (this.settings.getMode("Separator").equals("None")) {
                        String msg2 = FancyChatUtil.customSuffix(msg, " ", suffix, "", false);
                        isMadeByCustomChat = !isMadeByCustomChat;
                        if (isMadeByCustomChat) return;
                        e.setCancelled(true);
                        mc.player.sendChatMessage(msg2);
                    }
                    if (this.settings.getMode("Separator").equals("Default")) {
                        String msg2 = FancyChatUtil.customSuffix(msg, " | ", suffix, "", false);
                        isMadeByCustomChat = !isMadeByCustomChat;
                        if (isMadeByCustomChat) return;
                        e.setCancelled(true);
                        mc.player.sendChatMessage(msg2);
                    }
                    if (this.settings.getMode("Separator").equals("Brackets")) {
                        String msg2 = FancyChatUtil.customSuffix(msg, " < ", suffix, " > ", true);
                        isMadeByCustomChat = !isMadeByCustomChat;
                        if (isMadeByCustomChat) return;
                        e.setCancelled(true);
                        mc.player.sendChatMessage(msg2);
                    }
                }
                if (this.settings.getMode("Fancy chat type").equals("Retard")) {
                    String msg = FancyChatUtil.retardChat(packetMessage);
                    isMadeByCustomChat = !isMadeByCustomChat;
                    if (isMadeByCustomChat) return;
                    e.setCancelled(true);
//                    mc.player.sendChatMessage("msg1" +msg);
                    if (this.settings.getMode("Separator").equals("None")) {
                        String msg2 = FancyChatUtil.customSuffix(msg, " ", suffix, "", false);
                        isMadeByCustomChat = !isMadeByCustomChat;
                        if (isMadeByCustomChat) return;
                        e.setCancelled(true);
                        mc.player.sendChatMessage(msg2);
                    }
                    if (this.settings.getMode("Separator").equals("Default")) {
                        String msg2 = FancyChatUtil.customSuffix(msg, " | ", suffix, "", false);
                        isMadeByCustomChat = !isMadeByCustomChat;
                        if (isMadeByCustomChat) return;
                        e.setCancelled(true);
                        mc.player.sendChatMessage(msg2);
                    }
                    if (this.settings.getMode("Separator").equals("Brackets")) {
                        String msg2 = FancyChatUtil.customSuffix(msg, " < ", suffix, " > ", true);
                        isMadeByCustomChat = !isMadeByCustomChat;
                        if (isMadeByCustomChat) return;
                        e.setCancelled(true);
                        mc.player.sendChatMessage(msg2);
                    }
                }
                if (this.settings.getMode("Fancy chat type").equals("Spaced")) {
                    String msg = FancyChatUtil.spaces(packetMessage);
                    isMadeByCustomChat = !isMadeByCustomChat;
                    if (isMadeByCustomChat) return;
                    e.setCancelled(true);
//                    mc.player.sendChatMessage("msg1" +msg);
                    if (this.settings.getMode("Separator").equals("None")) {
                        String msg2 = FancyChatUtil.customSuffix(msg, " ", suffix, "", false);
                        isMadeByCustomChat = !isMadeByCustomChat;
                        if (isMadeByCustomChat) return;
                        e.setCancelled(true);
                        mc.player.sendChatMessage(msg2);
                    }
                    if (this.settings.getMode("Separator").equals("Default")) {
                        String msg2 = FancyChatUtil.customSuffix(msg, " | ", suffix, "", false);
                        isMadeByCustomChat = !isMadeByCustomChat;
                        if (isMadeByCustomChat) return;
                        e.setCancelled(true);
                        mc.player.sendChatMessage(msg2);
                    }
                    if (this.settings.getMode("Separator").equals("Brackets")) {
                        String msg2 = FancyChatUtil.customSuffix(msg, " < ", suffix, " > ", true);
                        isMadeByCustomChat = !isMadeByCustomChat;
                        if (isMadeByCustomChat) return;
                        e.setCancelled(true);
                        mc.player.sendChatMessage(msg2);
                    }
                }
                //Suffix Stuff

            }
        }
    }
}
