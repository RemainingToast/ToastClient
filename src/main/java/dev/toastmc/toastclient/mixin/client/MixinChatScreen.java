package dev.toastmc.toastclient.mixin.client;


import dev.toastmc.toastclient.ToastClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatScreen.class)
public class MixinChatScreen {

    @Shadow protected TextFieldWidget chatField;

    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;sendMessage(Ljava/lang/String;)V"))
    public void sendMessage(ChatScreen screen, String message) {
        if(message.startsWith(ToastClient.CMD_PREFIX) && MinecraftClient.getInstance().player != null){
            MinecraftClient.getInstance().inGameHud.getChatHud().addToMessageHistory(message);
            message = message.substring(1);
//            try {
//                Command.dispatcher.execute(message, MinecraftClient.getInstance().player.networkHandler.getCommandSource());
//            } catch (CommandSyntaxException e){
//                UtilKt.sendMessage(e.getMessage(), Color.RED);
//
//            }
            System.out.println(message);
        } else screen.sendMessage(message);
    }
}
