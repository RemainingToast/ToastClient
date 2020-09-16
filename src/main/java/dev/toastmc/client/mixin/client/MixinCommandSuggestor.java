package dev.toastmc.client.mixin.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import dev.toastmc.client.ToastClient;
import dev.toastmc.client.command.util.Command;
import dev.toastmc.client.util.TwoDRenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.command.CommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestor.class)
public abstract class MixinCommandSuggestor {

    @Shadow @Final private boolean slashRequired;

    @Shadow private ParseResults<CommandSource> parse;

    @Shadow @Final private TextFieldWidget textField;

    @Shadow private boolean completingSuggestions;

    @Shadow private CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow protected abstract void show();

    @Shadow @Final private Screen owner;

    @Inject(method = "refresh", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void refresh(CallbackInfo ci, String string, StringReader stringReader) {
        if(slashRequired) return;
        int i;
        if(stringReader.canRead() && stringReader.peek() == ToastClient.Companion.getCMD_PREFIX().charAt(0)){
            stringReader.skip();
            CommandDispatcher<CommandSource> commandDispatcher = Command.dispatcher;
            if(parse == null && MinecraftClient.getInstance().player != null) parse = commandDispatcher.parse(stringReader, MinecraftClient.getInstance().player.networkHandler.getCommandSource());
            i = textField.getCursor();
            if (i >= 1 && (!completingSuggestions)) {
                pendingSuggestions = commandDispatcher.getCompletionSuggestions(parse, i);
                pendingSuggestions.thenRun(() -> {
                    if (pendingSuggestions.isDone()) {
                        show();
                    }
                });
            }
            TwoDRenderUtils.drawHollowRect(new MatrixStack(), 2, this.owner.height - 14, this.owner.width - 2, this.owner.height - 2, 5, Color.RED.getRGB());
            ci.cancel();
        }
    }
}
