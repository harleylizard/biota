package com.harleylizard.ecosystem.config;

import net.minecraft.block.Block;

import java.util.function.Predicate;

public interface BlockWithMeta extends Comparable<BlockWithMeta>, Predicate<BlockWithMeta> {

    Block getBlock();

    int getMeta();

    @Override
    default int compareTo(BlockWithMeta o) {
        return getBlock() == o.getBlock() ? Integer.compare(getMeta(), o.getMeta()) : -1;
    }

    @Override
    default boolean test(BlockWithMeta blockWithMeta) {
        return getBlock() == blockWithMeta.getBlock() && getMeta() == blockWithMeta.getMeta();
    }

    static BlockWithMeta of(Block block, int meta) {
        return new StandardBlockWithMeta(block, meta);
    }

    final class StandardBlockWithMeta implements BlockWithMeta {
        private final Block block;
        private final int meta;

        private StandardBlockWithMeta(Block block, int meta) {
            this.block = block;
            this.meta = meta;
        }

        @Override
        public Block getBlock() {
            return block;
        }

        @Override
        public int getMeta() {
            return meta;
        }
    }
}
