package com.harleylizard.dynamicecosystem.mixin;

import com.harleylizard.dynamicecosystem.DynamicEcosystemWorld;
import com.harleylizard.dynamicecosystem.chunk.MutableNutrients;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WorldServer.class)
public final class WorldServerMixin implements DynamicEcosystemWorld {
    @Unique
    @Override
    public int get(int x, int y, int z) {
        WorldServer world = ((WorldServer) (Object) (this));
        if (!world.blockExists(x, y, z)) {
            return 0;
        }
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        return MutableNutrients.getOrCreate(chunk).get(x & 0x0F, y & 0x0F, z & 0x0F);
    }

    @Unique
    @Override
    public void set(int x, int y, int z, int nutrients) {
        WorldServer world = ((WorldServer) (Object) (this));
        if (!world.blockExists(x, y, z)) {
            return;
        }
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        MutableNutrients mutableNutrients = MutableNutrients.getOrCreate(chunk);
        mutableNutrients.push(world, x, y, z);
        mutableNutrients.set(x & 0x0F, y & 0x0F, z & 0x0F, nutrients);
        mutableNutrients.toClient(chunk);
    }

    @Unique
    @Override
    public void take(int x, int y, int z, int amount) {
        WorldServer world = ((WorldServer) (Object) (this));
        if (!world.blockExists(x, y, z)) {
            return;
        }
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        MutableNutrients mutableNutrients = MutableNutrients.getOrCreate(chunk);
        mutableNutrients.push(world, x, y, z);
        mutableNutrients.take(x & 0x0F, y & 0x0F, z & 0x0F, amount);
        mutableNutrients.toClient(chunk);
    }

    @Unique
    @Override
    public void add(int x, int y, int z, int amount) {
        WorldServer world = ((WorldServer) (Object) (this));
        if (!world.blockExists(x, y, z)) {
            return;
        }
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        MutableNutrients mutableNutrients = MutableNutrients.getOrCreate(chunk);
        mutableNutrients.push(world, x, y, z);
        mutableNutrients.add(x & 0x0F, y & 0x0F, z & 0x0F, amount);
        mutableNutrients.toClient(chunk);
    }
}
