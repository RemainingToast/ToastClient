package me.remainingtoast.toastclient.api.util.dummy;

import net.minecraft.tag.BlockTags;
import net.minecraft.world.dimension.DimensionType;

import java.util.OptionalLong;

public class DummyDimensionType extends DimensionType {

    private static DummyDimensionType instance;

    public static DummyDimensionType getInstance() {
        if (instance == null) instance = new DummyDimensionType();
        return instance;
    }

    private DummyDimensionType() {
        super(OptionalLong.empty(), true,  false, false, false, 1f, false, false, false, false, 0, BlockTags.INFINIBURN_OVERWORLD.getId(), DimensionType.OVERWORLD_ID, 1f);
    }
}
