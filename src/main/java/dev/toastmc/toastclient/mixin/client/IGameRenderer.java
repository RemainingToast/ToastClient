package dev.toastmc.toastclient.mixin.client;

import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface IGameRenderer {

    @Invoker
    void callLoadShader(Identifier identifier);

    @Accessor
    void setShader(ShaderEffect shader);

}
