package dev.toastmc.toastclient.mixin.client;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityVelocityUpdateS2CPacket.class)
public interface IEntityVelocityUpdateS2CPacket {

  @Accessor
  int getVelocityX();

  @Accessor
  void setVelocityX(int x);

  @Accessor
  int getVelocityY();

  @Accessor
  void setVelocityY(int y);

  @Accessor
  int getVelocityZ();

  @Accessor
  void setVelocityZ(int z);
}
