package com.harleylizard.ecosystem.world.chunk;

import com.harleylizard.ecosystem.world.BlockPos;

public interface PaletteSetter {

    void setBurnt(BlockPos blockPos);

    int setNutrients(BlockPos blockPos, int amount);

    int takeNutrients(BlockPos blockPos, int amount);

    int addNutrients(BlockPos blockPos, int amount);
}
