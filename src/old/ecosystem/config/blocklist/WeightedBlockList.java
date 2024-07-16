package com.harleylizard.ecosystem.config.blocklist;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.harleylizard.ecosystem.config.BlockWithMeta;

import java.util.Iterator;
import java.util.List;

public final class WeightedBlockList implements BlockList {
    public static final JsonDeserializer<WeightedBlockList> JSON_DESERIALIZER = (json, typeOfT, context) -> {
        JsonObject jsonObject = json.getAsJsonObject();
        return new WeightedBlockList(BlockList.deserialiseBlock(jsonObject), jsonObject.getAsJsonPrimitive("weight").getAsInt());
    };

    private final List<BlockWithMeta> list;
    private final int weight;

    private WeightedBlockList(List<BlockWithMeta> list, int getWeight) {
        this.list = list;
        this.weight = getWeight;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public Iterator<BlockWithMeta> iterator() {
        return list.iterator();
    }
}
