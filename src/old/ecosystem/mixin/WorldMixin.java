package com.harleylizard.ecosystem.mixin;

import com.harleylizard.ecosystem.DynamicEcosystemHelper;
import com.harleylizard.ecosystem.world.EcosystemWorld;
import com.harleylizard.ecosystem.world.MutableEcosystem;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(World.class)
public abstract class WorldMixin implements EcosystemWorld {

    @Shadow public abstract Chunk getChunkFromBlockCoords(int p_72938_1_, int p_72938_2_);

    @Override
    public int getNourishment(int x, int y, int z) {
        Chunk chunk = getChunkFromBlockCoords(x, z);
        return MutableEcosystem.get(chunk, y).getNourishment(x, y, z);
    }

    @Override
    public boolean removeNourishment(int x, int y, int z, int amount) {
        Chunk chunk = getChunkFromBlockCoords(x, z);
        MutableEcosystem ecosystem = MutableEcosystem.get(chunk, y);
        if (ecosystem.removeNourishment(x, y, z, amount)) {
            DynamicEcosystemHelper.sendEcosystem((World) (Object) this, x, y, z);
            return true;
        }
        return false;
    }

    @Override
    public void addNourishment(int x, int y, int z, int amount) {
        Chunk chunk = getChunkFromBlockCoords(x, z);
        MutableEcosystem ecosystem = MutableEcosystem.get(chunk, y);
        ecosystem.addNourishment(x, y, z, amount);
        DynamicEcosystemHelper.sendEcosystem((World) (Object) this, x, y, z);
    }
}
