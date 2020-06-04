package toast.client.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toast.client.event.EventManager;
import toast.client.event.events.player.EventRender;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Shadow
    private int ticks;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        ++this.ticks;
        EventRender event = new EventRender(this.ticks);
        EventManager.call(event);
        if(event.isCancelled()) ci.cancel();
    }
}
