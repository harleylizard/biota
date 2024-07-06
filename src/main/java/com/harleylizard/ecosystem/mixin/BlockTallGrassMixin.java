package com.harleylizard.ecosystem.mixin;

import com.harleylizard.ecosystem.world.EcosystemWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@Mixin(BlockTallGrass.class)
public final class BlockTallGrassMixin extends Block {

    protected BlockTallGrassMixin(Material materialIn) {
        super(materialIn);
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!worldIn.isRemote) {
            int nourishment = ((EcosystemWorld) worldIn).getNourishment(x, y, z);
            if (((EcosystemWorld) worldIn).removeNourishment(x, y - 4, z, 1)) {
                ((EcosystemWorld) worldIn).addNourishment(x, y, z, 4);
            }
            if (nourishment == 0 && ((EcosystemWorld) worldIn).getNourishment(x, y - 1, z) == 0 && random.nextInt(5) == 0) {
                worldIn.setBlock(x, y, z, Blocks.deadbush, 0, 3);
                ((EcosystemWorld) worldIn).removeNourishment(x, y, z, 16);
                return;
            }
            if (random.nextInt(10) == 0) {
                int range = 3;

                int v = 1;
                for (int j = -range; j < range; j++) for (int k = -range; k < range; k++) for (int l = -range; l < range; l++) {
                    int m = x + j;
                    int n = y + k;
                    int i = z + l;

                    if (worldIn.getBlock(m, n, i) instanceof BlockTallGrass && random.nextInt(5) == 0) {
                        v++;
                    }
                }
                float rainfall = worldIn.getBiomeGenForCoords(x, z).rainfall;
                ((EcosystemWorld) worldIn).removeNourishment(x, y, z, Math.min(v, !worldIn.canBlockSeeTheSky(x, y + 1, z) ? 12 : rainfall == 0.0F ? 8 : rainfall < 0.5F ? 4 : 1));
            }

            if (worldIn.canBlockSeeTheSky(x, y + 1, z) && random.nextInt(5) == 0) {
                ((EcosystemWorld) worldIn).addNourishment(x, y - 1, z, worldIn.isRainingAt(x, y + 1, z) ? 8 : 4);
            }
        }
    }
}
