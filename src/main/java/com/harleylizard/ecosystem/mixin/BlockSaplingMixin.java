package com.harleylizard.ecosystem.mixin;

import com.harleylizard.ecosystem.world.EcosystemWorld;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.BlockSapling;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(BlockSapling.class)
public final class BlockSaplingMixin {

    @Redirect(method = "growTree", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/feature/WorldGenerator;generate(Lnet/minecraft/world/World;Ljava/util/Random;III)Z"))
    public boolean growTree(WorldGenerator instance, World world, Random random, int x, int y, int z) {
        if (canGrow(world, x, y, z) && instance.generate(world, random, x, y, z)) {
            grow(world, x, y, z);
            return true;
        }
        return false;
    }

    @Unique
    private boolean canGrow(World world, int x, int y, int z) {
        if (!world.isRemote) {
            int range = 4;

            int nourishment = 0;
            for (int j = -range; j <= range; j++) for (int k = -range; k <= range; k++) for (int l = -range; l <= range; l++) {
                int distance = j * j + k * k + l * l;
                if (distance < range * range) {
                    int m = j + x;
                    int n = k + y;
                    int i = l + z;

                    nourishment += ((EcosystemWorld) world).getNourishment(m, n, i);
                }
            }
            return nourishment > 600;
        }
        return false;
    }

    @Unique
    private void grow(World world, int x, int y, int z) {
        int range = 4;
        for (int j = -range; j <= range; j++) for (int k = -range; k <= range; k++) for (int l = -range; l <= range; l++) {
            int distance = j * j + k * k + l * l;
            if (distance < range * range) {
                int m = j + x;
                int n = k + y;
                int i = l + z;

                float normal = (float) distance / (float) (range * range);

                ((EcosystemWorld) world).removeNourishment(m, n, i, (int) (16.0F * (1.0F - normal)));
            }
        }
    }
}
