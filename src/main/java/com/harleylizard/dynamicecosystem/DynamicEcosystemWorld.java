package com.harleylizard.dynamicecosystem;

import com.harleylizard.dynamicecosystem.chunk.NutrientsAccessor;
import net.minecraft.world.World;

public interface DynamicEcosystemWorld extends NutrientsAccessor {

    static DynamicEcosystemWorld get(World world) {
       if (world.isRemote) {
           throw new RuntimeException("Mustn't use a client world");
       }
       return (DynamicEcosystemWorld) world;
    }
}
