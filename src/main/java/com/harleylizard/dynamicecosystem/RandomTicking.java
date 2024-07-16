package com.harleylizard.dynamicecosystem;

import net.minecraft.world.World;

public interface RandomTicking {

    void randomTick(DynamicEcosystemWorld dynamicEcosystemWorld, World world, int x, int y, int z, int meta);
}
