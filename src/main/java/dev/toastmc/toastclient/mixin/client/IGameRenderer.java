package dev.toastmc.toastclient.mixin.client;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameRenderer.class)
public interface IGameRenderer {

    /*@Invoker
    void callLoadShader(Identifier identifier);

    @Accessor
    void setShader(ShaderEffect shader);*/

}
