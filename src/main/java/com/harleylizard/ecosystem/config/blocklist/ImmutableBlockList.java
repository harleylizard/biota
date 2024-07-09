package com.harleylizard.ecosystem.config.blocklist;

import com.google.gson.JsonDeserializer;
import com.harleylizard.ecosystem.config.BlockWithMeta;

import java.util.Iterator;
import java.util.List;

public final class ImmutableBlockList implements BlockList {
    public static final JsonDeserializer<BlockList> JSON_DESERIALIZER = (json, typeOfT, context) -> new ImmutableBlockList(BlockList.deserialiseBlock(json.getAsJsonObject()));

    private final List<BlockWithMeta> list;

    private ImmutableBlockList(List<BlockWithMeta> list) {
        this.list = list;
    }

    @Override
    public Iterator<BlockWithMeta> iterator() {
        return list.iterator();
    }
}
