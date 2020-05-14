package toast.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class MovementUtil {
    private static MinecraftClient mc = MinecraftClient.getInstance();

    public static void lookPacket(double yaw, double pitch) {
        if(mc.player == null) return;
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly((float) yaw, (float) pitch, mc.player.onGround));
    }

    public static void lookClient(double yaw, double pitch) {
        if(mc.player == null) return;
        mc.player.yaw = (float) yaw;
        mc.player.pitch = (float) pitch;
    }

    public static void lookAt(Vec3d point, boolean packet) {
        if(mc.player == null) return;
        double lookx = mc.player.getX() - point.getX();
        double looky = mc.player.getY() - point.getY();
        double lookz = mc.player.getZ() - point.getZ();

        double len = Math.sqrt(lookx * lookx + looky * looky + lookz * lookz);

        lookx /= len;
        looky /= len;
        lookz /= len;

        double pitch = Math.asin(looky);
        double yaw = Math.atan2(lookz, lookx);

        //to degree
        pitch = pitch * 180.0 / Math.PI;
        yaw = yaw * 180.0 / Math.PI;
        yaw += 90f;
        if(packet) {
            lookPacket(yaw, pitch);
        } else {
            lookClient(yaw, pitch);
        }
    }

}
