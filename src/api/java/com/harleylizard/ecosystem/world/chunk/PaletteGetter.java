package com.harleylizard.ecosystem.world.chunk;

import com.harleylizard.ecosystem.world.BlockPos;

public interface PaletteGetter {

    boolean isBurnt(BlockPos blockPos);

    int getNutrients(BlockPos blockPos);
}
