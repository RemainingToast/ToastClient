package toast.client.utils;

import com.google.common.eventbus.Subscribe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;
import toast.client.events.network.EventPacketReceived;
import toast.client.events.network.EventSyncedUpdate;
import toast.client.events.player.EventUpdate;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import static toast.client.ToastClient.eventBus;

@Environment(EnvType.CLIENT)
public class TPSCalculator {
    public static TPSCalculator calculatorInstance;
    public static double tps;
    private static Timer timer;

    private long lastUpdateMillis = 0;
    private final float[] ticks = new float[10];
    private double nextPredictedTick = Double.MAX_VALUE;
    private int nextToCheck = 0;

    public TPSCalculator() {
        eventBus.register(this);
        lastUpdateMillis = 0;
        tps = 20;
        Arrays.fill(ticks, 0);
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (System.currentTimeMillis() >= nextPredictedTick) {
                        EventSyncedUpdate event = new EventSyncedUpdate();
                        eventBus.post(event);
                    }
                }
            }, 0, 1);
        }
    }

    @Subscribe
    public void onPacketReceived(EventPacketReceived event) {
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            ticks[nextToCheck] = (float) (MathHelper.clamp(1000d / (System.currentTimeMillis() - lastUpdateMillis), 0, 20));
            lastUpdateMillis = System.currentTimeMillis();
            double totalTicks = 0;
            int ticksStored = 0;
            for (float i : ticks) {
                if (i > 0) {
                    totalTicks += i;
                    ticksStored++;
                }
            }
            tps = MathHelper.clamp(totalTicks / ticksStored, 0, 20);
            nextPredictedTick = System.currentTimeMillis() + 1000 / tps;
        }
        if (nextToCheck > ticks.length - 1) nextToCheck = 0;
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        if (MinecraftClient.getInstance().world == null) {
            lastUpdateMillis = 0;
            tps = 20;
            Arrays.fill(ticks, 0);
        }
    }
}
