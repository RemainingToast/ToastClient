package me.remainingtoast.toastclient.api.util.dummy;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DummyClientPlayerEntity extends ClientPlayerEntity {

    private static DummyClientPlayerEntity instance;
    private Identifier skinIdentifier = null;
    private Identifier capeIdentifier = null;

    public static DummyClientPlayerEntity getInstance() {
        if (instance == null) instance = new DummyClientPlayerEntity(null);
        return instance;
    }

    private DummyClientPlayerEntity(World world) {
        super(MinecraftClient.getInstance(), DummyClientWorld.getInstance(), DummyClientPlayNetworkHandler.getInstance(), null, null,false, false);
        MinecraftClient.getInstance().getSkinProvider().loadSkin(getGameProfile(), (type, identifier, texture) -> {
            if(type.equals(MinecraftProfileTexture.Type.SKIN)){
                skinIdentifier = identifier;
            } else if (type.equals(MinecraftProfileTexture.Type.CAPE)) {
                capeIdentifier = identifier;
            }
        }, true);
    }

    @Override
    public boolean hasSkinTexture() {
        return true;
    }

    @Override
    public Identifier getSkinTexture() {
        return skinIdentifier == null ? DefaultSkinHelper.getTexture(this.getUuid()) : skinIdentifier;
    }

    @Override
    public Identifier getCapeTexture() {
        return capeIdentifier;
    }

    @Nullable
    @Override
    protected PlayerListEntry getPlayerListEntry() {
        return null;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return true;
    }
}

