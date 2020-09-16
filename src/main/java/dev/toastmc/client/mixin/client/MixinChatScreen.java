package dev.toastmc.client.mixin.client;


import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.toastmc.client.ToastClient;
import dev.toastmc.client.command.util.Command;
import dev.toastmc.client.util.Color;
import dev.toastmc.client.util.UtilKt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatScreen.class)
public class MixinChatScreen {

    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;sendMessage(Ljava/lang/String;)V"))
    public void sendMessage(ChatScreen screen, String message) {
        if(message.startsWith(ToastClient.Companion.getCMD_PREFIX()) && MinecraftClient.getInstance().player != null){
            MinecraftClient.getInstance().inGameHud.getChatHud().addToMessageHistory(message);
            message = message.substring(1);
            try {
                Command.dispatcher.execute(message, MinecraftClient.getInstance().player.networkHandler.getCommandSource());
            } catch (CommandSyntaxException e){
                UtilKt.sendMessage(e.getMessage(), Color.RED);
                System.out.println(e.getMessage());
            }
        } else screen.sendMessage(message);
    }
}
