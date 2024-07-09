package com.harleylizard.ecosystem.config;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

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

    static BlockWithMeta of(World world, int x, int y, int z) {
        return new ImmutableBlockWithMeta(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
    }

    static BlockWithMeta of(Block block, int meta) {
        return new ImmutableBlockWithMeta(block, meta);
    }

    static Block of(String name) {
        return requireNonNull(Block.getBlockFromName(name), "Failed to get block " + name);
    }

    final class ImmutableBlockWithMeta implements BlockWithMeta {
        public static final JsonDeserializer<BlockWithMeta> JSON_DESERIALIZER = (json, typeOfT, context) -> {
            JsonObject jsonObject = json.getAsJsonObject();
            String name = jsonObject.getAsJsonPrimitive("name").getAsString();
            Block block = of(name);
            return new ImmutableBlockWithMeta(block, jsonObject.getAsJsonPrimitive("meta").getAsInt());
        };

        private final Block block;
        private final int meta;

        private ImmutableBlockWithMeta(Block block, int meta) {
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
