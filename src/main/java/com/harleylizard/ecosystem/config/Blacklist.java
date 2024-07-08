package com.harleylizard.ecosystem.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.Loader;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Blacklist {
    private static final Blacklist EMPTY = new Blacklist(Collections.emptyList());

    public static final JsonDeserializer<Blacklist> JSON_DESERIALIZER = (json, typeOfT, context) -> {
        JsonArray jsonArray = json.getAsJsonArray();

        List<BiomeGenBase> list1 = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();

            String modId = jsonObject.getAsJsonPrimitive("mod-id").getAsString();
            if (Loader.isModLoaded(modId)) {
                JsonArray jsonArray1 = jsonObject.getAsJsonArray("biomes");
                for (JsonElement jsonElement : jsonArray1) {
                    for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
                        if (biome.biomeName.equals(jsonElement.getAsString())) {
                            list1.add(biome);
                        }
                    }
                }
            }
        }
        return list1.isEmpty() ? of() : new Blacklist(Collections.unmodifiableList(list1));
    };

    private final List<BiomeGenBase> list;

    private Blacklist(List<BiomeGenBase> list) {
        this.list = list;
    }

    public boolean isBlacklisted(BiomeGenBase biome) {
        return list.contains(biome);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public static Blacklist of() {
        return EMPTY;
    }
}
