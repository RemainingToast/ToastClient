package toast.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WolfEntity;

public class EntityUtils {

    private static MinecraftClient mc = MinecraftClient.getInstance();

    public static boolean notSelf(Entity e) {
        return e != mc.player && e != mc.cameraEntity;
    }

    public static boolean isAnimal(Entity e) {
        return e instanceof AnimalEntity ||
                e instanceof AmbientEntity ||
                e instanceof WaterCreatureEntity ||
                (e instanceof GolemEntity && !((GolemEntity) e).isHandSwinging) ||
                e instanceof VillagerEntity;
    }

    public static boolean isLiving(Entity e) {
        return e instanceof LivingEntity;
    }

    public static boolean isHostile(Entity e) {
        return (e instanceof HostileEntity && !(e instanceof ZombiePigmanEntity) && !(e instanceof EndermanEntity)) ||
                (e instanceof ZombiePigmanEntity && ((ZombiePigmanEntity) e).isAngryAt(mc.player)) ||
                (e instanceof WolfEntity && ((WolfEntity) e).isAngry() && ((WolfEntity) e).getOwnerUuid() != mc.player.getUuid()) ||
                (e instanceof EndermanEntity && ((EndermanEntity) e).isAngry()) ||
                (e instanceof GolemEntity && ((GolemEntity) e).isHandSwinging);
    }

    public static boolean isNeutral(Entity e) {
        return (e instanceof ZombiePigmanEntity && !((ZombiePigmanEntity) e).isAngryAt(mc.player)) ||
                (e instanceof WolfEntity && (!((WolfEntity) e).isAngry() || ((WolfEntity) e).getOwnerUuid() == mc.player.getUuid())) ||
                (e instanceof EndermanEntity && !((EndermanEntity) e).isAngry()) ||
                (e instanceof GolemEntity && !((GolemEntity) e).isHandSwinging);
    }

}
