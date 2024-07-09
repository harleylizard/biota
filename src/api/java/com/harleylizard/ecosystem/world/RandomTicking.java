package com.harleylizard.ecosystem.world;

import java.util.Random;

public interface RandomTicking {

    void randomTick(DynamicEcosystemWorld world, BlockPos blockPos, int meta, Random random);
}
