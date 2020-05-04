package toast.client.modules.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import toast.client.event.EventImpl;
import toast.client.event.events.player.EventUpdate;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;

public class Fullbright extends Module {

    private static boolean increasedGamma = false;
    private static double previousGamma = 0;

    public Fullbright() {
        super("Fullbright", Module.Category.RENDER, -1);
        this.addModes("Potion", "Gamma");
    }

    @EventImpl
    public void onTick(EventUpdate event) {
        if(this.isMode("Gamma") && !increasedGamma && mc.options.gamma < 16) {
            increaseGamma();
        }
        else if(this.isMode("Potion")) {
            assert mc.player != null;
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 1, 4));
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.options.gamma = previousGamma;
        increasedGamma = false;
        assert mc.player != null;
        mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        previousGamma = mc.options.gamma;
    }

    private void increaseGamma() {
        mc.options.gamma = 1000;
        increasedGamma = true;
    }
}
