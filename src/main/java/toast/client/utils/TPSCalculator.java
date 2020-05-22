package toast.client.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;
import toast.client.event.EventImpl;
import toast.client.event.EventManager;
import toast.client.event.events.network.EventPacketReceived;
import toast.client.event.events.network.EventSyncedUpdate;
import toast.client.event.events.player.EventUpdate;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

@Environment(EnvType.CLIENT)
public class TPSCalculator {
    public static TPSCalculator calculatorInstance;
    public static double tps;
    private static Timer timer;

    private long lastUpdateMillis = 0;
    private float[] ticks = new float[10];
    private double nextPredictedTick = Double.MAX_VALUE;
    private int nextToCheck = 0;

    public void TPSCalculator() {
        EventManager.register(this);
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
                        EventManager.call(event);
                    }
                }
            }, 0, 1);
        }
    }

    @EventImpl
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
    }

    @EventImpl
    public void onUpdate(EventUpdate event) {
        if (MinecraftClient.getInstance().world == null) {
            lastUpdateMillis = 0;
            tps = 20;
            Arrays.fill(ticks, 0);
        }
    }
}
