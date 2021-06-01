package dev.toastmc.toastclient.mixin.client;


import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.toastmc.toastclient.api.managers.command.Command;
import dev.toastmc.toastclient.api.managers.command.CommandManager;
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

    @Redirect(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;sendMessage(Ljava/lang/String;)V"),
            method = {"keyPressed"}
    )
    private void on(ChatScreen screen, String message) {
        if(message.startsWith(CommandManager.prefix) && MinecraftClient.getInstance().player != null){
            MinecraftClient.getInstance().inGameHud.getChatHud().addToMessageHistory(message);
            message = message.substring(1);
            try {
                Command.dispatcher.execute(message, MinecraftClient.getInstance().player.networkHandler.getCommandSource());
            } catch (CommandSyntaxException ignored){ }
        } else screen.sendMessage(message);
    }
}
