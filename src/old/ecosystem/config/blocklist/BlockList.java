package com.harleylizard.ecosystem.config.blocklist;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.harleylizard.ecosystem.config.BlockWithMeta;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public interface BlockList extends Predicate<BlockWithMeta>, Iterable<BlockWithMeta> {

    @Override
    default boolean test(BlockWithMeta blockWithMeta) {
        for (BlockWithMeta block : this) {
            if (block.test(blockWithMeta)) {
                return true;
            }
        }
        return false;
    }

    static List<BlockWithMeta> deserialiseBlock(JsonObject jsonObject) {
        String name = jsonObject.getAsJsonPrimitive("name").getAsString();
        Block block = BlockWithMeta.of(name);
        List<BlockWithMeta> list = new ArrayList<>();
        for (JsonElement element : jsonObject.getAsJsonArray("meta")) {
            list.add(BlockWithMeta.of(block, element.getAsInt()));
        }
        return list.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(list);
    }
}
