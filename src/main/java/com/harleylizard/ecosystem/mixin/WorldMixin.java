package com.harleylizard.ecosystem.mixin;

import com.harleylizard.ecosystem.ChunkQueue;
import com.harleylizard.ecosystem.Ecosystem;
import com.harleylizard.ecosystem.EcosystemGetter;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class WorldMixin implements EcosystemGetter {
    @Unique
    private final ChunkQueue chunkQueue = new ChunkQueue();

    @Shadow public abstract Chunk getChunkFromBlockCoords(int p_72938_1_, int p_72938_2_);

    @Shadow public boolean isRemote;

    @Override
    public int getNourishment(int x, int y, int z) {
        Chunk chunk = getChunkFromBlockCoords(x, z);
        Ecosystem ecosystem = Ecosystem.get(chunk);
        return ecosystem == null ? 0 : ecosystem.getNourishment(chunk, x, y, z);
    }

    @Override
    public boolean takeNourishment(int x, int y, int z, int amount) {
        Chunk chunk = getChunkFromBlockCoords(x, z);
        Ecosystem ecosystem = Ecosystem.getOrCreate(chunk);
        if (ecosystem.takeNourishment(chunk, x, y, z, amount)) {
            chunkQueue.push(chunk, ecosystem, x, y, z);
            return true;
        }
        return false;
    }

    @Override
    public void addNourishment(int x, int y, int z, int amount) {
        Chunk chunk = getChunkFromBlockCoords(x, z);
        Ecosystem ecosystem = Ecosystem.getOrCreate(chunk);
        ecosystem.addNourishment(chunk, x, y, z, amount);
        chunkQueue.push(chunk, ecosystem, x, y, z);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if (!isRemote) {
            chunkQueue.poll();
        }
    }
}
