package com.harleylizard.ecosystem.world.chunk;

import com.harleylizard.ecosystem.world.BlockPos;

public final class EmptyEnvironment implements Environment {
    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean isBurnt(BlockPos blockPos) {
        return false;
    }

    @Override
    public int getNutrients(BlockPos blockPos) {
        return 0;
    }
}
