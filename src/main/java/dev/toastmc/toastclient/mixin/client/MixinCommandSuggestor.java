package dev.toastmc.toastclient.mixin.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import dev.toastmc.toastclient.api.managers.command.Command;
import dev.toastmc.toastclient.api.managers.command.CommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;

@Mixin(ChatInputSuggestor.class)
public abstract class MixinCommandSuggestor {

    @Shadow private ParseResults<CommandSource> parse;

    @Shadow @Final private TextFieldWidget textField;

    @Shadow private boolean completingSuggestions;

    @Shadow private CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow public abstract void show(boolean narrateFirstSuggestion);

    @Inject(
            at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z"),
            method = {"refresh"},
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    public void on(CallbackInfo ci, String string, StringReader stringReader) {
        int i;
        if(stringReader.canRead() && stringReader.peek() == CommandManager.prefix.charAt(0)){
            stringReader.skip();
            CommandDispatcher<CommandSource> commandDispatcher = Command.dispatcher;
            if(parse == null && MinecraftClient.getInstance().player != null) parse = commandDispatcher.parse(stringReader, MinecraftClient.getInstance().player.networkHandler.getCommandSource());
            i = textField.getCursor();
            if (i >= 1 && (!completingSuggestions)) {
                pendingSuggestions = commandDispatcher.getCompletionSuggestions(parse, i);
                pendingSuggestions.thenRun(() -> {
                    if (pendingSuggestions.isDone()) {
                        show(false);
                    }
                });
            }
            ci.cancel();
        }
    }
}
