package com.harleylizard.ecosystem.config;

import com.google.gson.*;
import com.harleylizard.ecosystem.config.blocklist.WeightedBlockList;
import cpw.mods.fml.common.Loader;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Influence implements Comparable<Influence> {
    private static final JsonDeserializer<Influence> JSON_DESERIALIZER = (json, typeOfT, context) -> {
        JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.has("mod-id") && !Loader.isModLoaded(jsonObject.getAsJsonPrimitive("mod-id").getAsString())) {
            return null;
        }
        String name = jsonObject.getAsJsonPrimitive("biome").getAsString();
        BiomeGenBase biome = getBiome(name);
        if (biome == null) {
            throw new JsonIOException("Failed to get biome named " + name);
        }
        BiomeGenBase prerequisiteBiome = !jsonObject.has("prerequisite-biome") ? null : getBiome(jsonObject.getAsJsonPrimitive("prerequisite-biome").getAsString());

        List<WeightedBlockList> list1 = new ArrayList<>();
        for (JsonElement element : jsonObject.getAsJsonArray("blocks")) {
            list1.add(context.deserialize(element, WeightedBlockList.class));
        }
        return new Influence(
                Collections.unmodifiableList(list1),
                jsonObject.has("blacklist") ? Blacklist.of() : context.deserialize(jsonObject.get("blacklist"), Blacklist.class),
                biome,
                prerequisiteBiome,
                context.deserialize(jsonObject.get("priority"), Priority.class),
                jsonObject.getAsJsonPrimitive("filter").getAsInt());
    };

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(WeightedBlockList.class, WeightedBlockList.JSON_DESERIALIZER)
            .registerTypeAdapter(Priority.class, Priority.JSON_DESERIALIZER)
            .registerTypeAdapter(Blacklist.class, Blacklist.JSON_DESERIALIZER)
            .registerTypeAdapter(Influence.class, JSON_DESERIALIZER)
            .create();

    private final List<WeightedBlockList> list;
    private final Blacklist blacklist;
    private final BiomeGenBase biome;
    private final BiomeGenBase prerequisiteBiome;
    private final Priority priority;
    private final int filter;

    private Influence(List<WeightedBlockList> list, Blacklist blacklist, BiomeGenBase biome, BiomeGenBase prerequisiteBiome, Priority priority, int filter) {
        this.list = list;
        this.blacklist = blacklist;
        this.biome = biome;
        this.prerequisiteBiome = prerequisiteBiome;
        this.priority = priority;
        this.filter = filter;
    }

    public boolean canInfluence(World world, int x, int y, int z) {
        if (!blacklist.isEmpty() && blacklist.isBlacklisted(world.getBiomeGenForCoords(x, z))) {
            return false;
        }
        int range = 4;
        int i = 0;
        for (int j = -range; j <= range; j++) for (int k = -range; k <= range; k++) for (int l = -range; l <= range; l++) {
            int x1 = x + j;
            int y1 = y + k;
            int z1 = z + l;
            if (x1 == x && y1 == y && z1 == z) {
                continue;
            }
            if (world.blockExists(x1, y1, z1)) {
                Block block = world.getBlock(x1, y1, z1);
                int meta = world.getBlockMetadata(x1, y1, z1);

                for (WeightedBlockList weightedBlockList : list) {
                    if (weightedBlockList.test(BlockWithMeta.of(block, meta))) {
                        i += weightedBlockList.getWeight();
                    }
                }
            }
        }
        return i >= filter;
    }

    public BiomeGenBase getBiome() {
        return biome;
    }

    public BiomeGenBase getPrerequisiteBiome() {
        return prerequisiteBiome;
    }

    @Override
    public int compareTo(Influence o) {
        return priority.compareTo(o.priority);
    }

    private static BiomeGenBase getBiome(String name) {
        for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
            if (biome != null && biome.biomeName.equals(name)) {
                return biome;
            }
        }
        return null;
    }

    public enum Priority {
        HIGHEST,
        HIGH,
        LOW;

        private static final JsonDeserializer<Priority> JSON_DESERIALIZER = (json, typeOfT, context) -> fromString(json.getAsString());

        private static Priority fromString(String name) {
            switch (name) {
                case "low": return LOW;
                case "high": return HIGH;
                case "highest": return HIGHEST;
                default: throw new RuntimeException("Unknown priority " + name);
            }
        }
    }
}
