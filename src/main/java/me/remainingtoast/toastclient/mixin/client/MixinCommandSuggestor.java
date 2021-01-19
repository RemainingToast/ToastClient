package me.remainingtoast.toastclient.mixin.client;

import com.mojang.brigadier.StringReader;
import net.minecraft.client.gui.screen.CommandSuggestor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CommandSuggestor.class)
public abstract class MixinCommandSuggestor {

    @Shadow @Final private boolean slashOptional;

    @Inject(method = "refresh", at = @At(value = "HEAD", target = "Lcom/mojang/brigadier/StringReader;canRead()Z"), cancellable = true)
    public void refresh(CallbackInfo ci, String string, StringReader stringReader) {
        if(!slashOptional) return;
//        int i;
//        hud.setPrefixPresent(false);
//        if(stringReader.canRead() && stringReader.peek() == ToastClient.Companion.getCMD_PREFIX().charAt(0)){
//            hud.setPrefixPresent(true);
//            stringReader.skip();
//            CommandDispatcher<CommandSource> commandDispatcher = Command.dispatcher;
//            if(parse == null && MinecraftClient.getInstance().player != null) parse = commandDispatcher.parse(stringReader, MinecraftClient.getInstance().player.networkHandler.getCommandSource());
//            i = textField.getCursor();
//            if (i >= 1 && (!completingSuggestions)) {
//                pendingSuggestions = commandDispatcher.getCompletionSuggestions(parse, i);
//                pendingSuggestions.thenRun(() -> {
//                    if (pendingSuggestions.isDone()) {
//                        show();
//                    }
//                });
//            }
//            ci.cancel();
//        }
    }
}
