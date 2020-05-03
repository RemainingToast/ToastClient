package toast.client.modules.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import toast.client.event.EventImpl;
import toast.client.event.events.player.EventUpdate;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;

public class Fullbright extends Module {

    public Fullbright() {
        super("Fullbright", Module.Category.RENDER, -1);
        this.addModes("Potion", "Gamma");
    }

    @EventImpl
    public void onTick(EventUpdate event) {
        if(this.isMode("Gamma")) {
            if(mc.options.gamma < 16) mc.options.gamma += 1.2;

        } else if(this.isMode("Potion")) {
            assert mc.player != null;
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 1, 4));
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.options.gamma = 1;
        assert mc.player != null;
        mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
    }
}
