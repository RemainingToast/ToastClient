package me.remainingtoast.toastclient.api.util.dummy;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.Difficulty;
import net.minecraft.world.dimension.DimensionType;

public class DummyClientWorld extends ClientWorld {

    private static DummyClientWorld instance;

    public static DummyClientWorld getInstance() {
        if (instance == null) instance = new DummyClientWorld();
        return instance;
    }

    private DummyClientWorld() {
        super(DummyClientPlayNetworkHandler.getInstance(), new Properties(Difficulty.PEACEFUL, false, true), null, DummyDimensionType.getInstance(), 0, () -> null, null, false, 0L);
    }
}
