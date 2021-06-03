package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.impl.module.misc.ExtraTab;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud {

  @Redirect(
      at = @At(value = "INVOKE", target = "Ljava/util/List;subList(II)Ljava/util/List;"),
      method = {"render"})
  public <E> List<E> render(List<E> list, int fromIndex, int toIndex) {
    return list.subList(
        fromIndex,
        ExtraTab.INSTANCE.isEnabled()
            ? Math.min(ExtraTab.INSTANCE.getTabSize().getIntValue(), list.size())
            : toIndex);
  }

  @Inject(
      at = {@At("HEAD")},
      method = {"getPlayerName"},
      cancellable = true)
  public void getPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
    if (ExtraTab.INSTANCE.isEnabled()) {
      cir.setReturnValue(ExtraTab.INSTANCE.formatList(entry));
      cir.cancel();
    }
  }
}
