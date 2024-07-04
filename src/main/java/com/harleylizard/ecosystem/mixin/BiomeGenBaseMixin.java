package com.harleylizard.ecosystem.mixin;

import com.harleylizard.ecosystem.Ecosystem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeGenBase.class)
public final class BiomeGenBaseMixin {

    @Inject(method = "getBiomeGrassColor", at = @At("RETURN"), cancellable = true, remap = false)
    public void getBiomeGrassColor(int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
        Chunk chunk = Minecraft.getMinecraft().theWorld.getChunkFromBlockCoords(x, z);
        Ecosystem ecosystem = Ecosystem.get(chunk);
        if (ecosystem == null) {
            return;
        }

        int nourishment = ecosystem.getNourishment(x, y, z);
        if (nourishment < 0) {
            cir.setReturnValue(0xFFFFFF);
        }
    }
}
