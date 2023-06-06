package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.impl.module.misc.ExtraTab;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Comparator;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud {

    @Shadow @Final private static Comparator<PlayerListEntry> ENTRY_ORDERING;

    @Shadow @Final private MinecraftClient client;

    @Redirect(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;getListedPlayerListEntries()Ljava/util/Collection;"),
            method = {"collectPlayerEntries"}
    )
    public <E> Collection<PlayerListEntry> render(ClientPlayNetworkHandler instance) {
        assert this.client.player != null;
        return this.client.player.networkHandler.getListedPlayerListEntries()
                .stream()
                .sorted(ENTRY_ORDERING)
                .limit(ExtraTab.INSTANCE.isEnabled() ? ExtraTab.INSTANCE.getTabSize().getIntValue() : 80L)
                .toList();
    }

    @Inject(
            at = {@At("HEAD")},
            method = {"getPlayerName"},
            cancellable = true
    )
    public void getPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir){
        if(ExtraTab.INSTANCE.isEnabled()){
            cir.setReturnValue(ExtraTab.INSTANCE.formatList(entry));
            cir.cancel();
        }
    }
}
