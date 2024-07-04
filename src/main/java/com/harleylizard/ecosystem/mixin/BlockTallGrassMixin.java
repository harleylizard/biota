package com.harleylizard.ecosystem.mixin;

import com.harleylizard.ecosystem.EcosystemGetter;
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
            int nourishment = ((EcosystemGetter) worldIn).getNourishment(x, y, z);

            if (nourishment > 0 && random.nextInt(5) == 0) {
                ((EcosystemGetter) worldIn).takeNourishment(x, y, z, 1);
            }
            if (((EcosystemGetter) worldIn).takeNourishment(x, y - 1, z, 1)) {
                ((EcosystemGetter) worldIn).addNourishment(x, y, z, 1);
            }
            if (nourishment == 0 && ((EcosystemGetter) worldIn).getNourishment(x, y - 1, z) <= 8) {
                worldIn.setBlock(x,  y, z, Blocks.deadbush, 0, 3);
            }
        }
    }
}
