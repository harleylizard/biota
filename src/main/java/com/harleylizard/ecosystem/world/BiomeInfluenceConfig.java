package com.harleylizard.ecosystem.world;

import com.google.gson.*;
import cpw.mods.fml.common.Loader;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.io.*;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class BiomeInfluenceConfig {
    private static final Map<PriorityInfluence, Set<PriorityInfluence>> MAP = new HashMap<>();

    private static final JsonDeserializer<PriorityInfluence> JSON_DESERIALIZER = (json, typeOfT, context) -> {
        JsonObject jsonObject = json.getAsJsonObject();

        List<BiomeInfluence.Influence> influences = new ArrayList<>();
        for (JsonElement element1 : jsonObject.getAsJsonArray("blocks")) {
            influences.add(createInfluence(element1));
        }

        PriorityInfluence priorityInfluence = new PriorityInfluence(Collections.unmodifiableList(influences), getBiome(jsonObject.getAsJsonPrimitive("biome").getAsString()), Priority.fromString(jsonObject.getAsJsonPrimitive("priority").getAsString()), jsonObject.getAsJsonPrimitive("filter").getAsInt());
        if (jsonObject.has("prerequisite-biome")) {
            priorityInfluence.prerequisite = jsonObject.getAsJsonPrimitive("prerequisite-biome").getAsString();
        }
        return priorityInfluence;
    };
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(PriorityInfluence.class, JSON_DESERIALIZER).create();

    private static final List<String> JSON_FILES;

    static {
        List<String> list = new ArrayList<>();
        list.add("data/sunflower_plains.json");
        list.add("data/jungle.json");
        list.add("data/birch_forest.json");
        list.add("data/forest.json");
        list.add("data/plains.json");

        JSON_FILES = Collections.unmodifiableList(list);
    }

    private final List<PriorityInfluence> list;

    private BiomeInfluenceConfig(List<PriorityInfluence> list) {
        this.list = list;
        for (PriorityInfluence priorityInfluence : list) {
            String prerequisite = priorityInfluence.prerequisite;
            if (prerequisite != null && !prerequisite.isEmpty()) {

                PriorityInfluence parent = null;
                for (PriorityInfluence key : list) {
                    if (priorityInfluence == key) {
                        continue;
                    }
                    if (key.biome != null && key.biome.biomeName.equals(prerequisite)) {
                        parent = key;
                        break;
                    }
                }
                if (parent != null) {
                    MAP.computeIfAbsent(parent, ignored -> new HashSet<>()).add(priorityInfluence);
                }
            }
        }
    }

    public BiomeGenBase getBiomeFor(World world, int x, int y, int z) {
        for (PriorityInfluence priorityInfluence : list) {
            if (priorityInfluence.canInfluence(world, x, y, z)) {
                if (MAP.containsKey(priorityInfluence)) {
                    for (PriorityInfluence variant : MAP.get(priorityInfluence)) {
                        if (variant.canInfluence(world, x, y, z)) {
                            return variant.biome;
                        }
                    }
                }
                return priorityInfluence.biome;
            }
        }
        return null;
    }

    public static BiomeInfluenceConfig createConfig() {
        try {
            Path directory = Paths.get("config", "dynamic-ecosystem");

            List<PriorityInfluence> list = new ArrayList<>();
            if (!Files.isDirectory(directory)) {
                Files.createDirectories(directory);
                for (String jsonFile : JSON_FILES) {
                    Path path = directory.resolve(jsonFile.substring(jsonFile.lastIndexOf("/") + 1));

                    URL url = BiomeInfluenceConfig.class.getClassLoader().getResource(jsonFile);
                    try (InputStream stream = url.openStream(); OutputStream out = Files.newOutputStream(path)) {
                        int byteRead;
                        while ((byteRead = stream.read()) != -1) {
                            out.write(byteRead);
                        }
                    }
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                        list.add(GSON.fromJson(reader, PriorityInfluence.class));
                    }
                }
            } else {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, path -> path.toString().endsWith(".json"))) {
                    for (Path path : stream) {
                        try (BufferedReader reader = Files.newBufferedReader(path)) {
                            list.add(GSON.fromJson(reader, PriorityInfluence.class));
                        }
                    }
                }
            }
            return new BiomeInfluenceConfig(list.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(list));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static BiomeInfluence.Influence createInfluence(JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();

        Block block = null;
        String name = jsonObject.getAsJsonPrimitive("name").getAsString();
        if (name.contains(":")) {
            String[] split = name.split(":", 2);
            String modId = split[0];
            String blockName = split[1];
            if (Loader.isModLoaded(modId) || modId.equalsIgnoreCase("minecraft")) {
                block = Block.getBlockFromName(blockName);
            }
        } else {
            block = Block.getBlockFromName(name);
        }
        return BiomeInfluence.influence(
                block,
                jsonObject.getAsJsonPrimitive("meta").getAsInt(),
                jsonObject.getAsJsonPrimitive("weight").getAsInt()
        );
    }

    private static BiomeGenBase getBiome(String name) {
        for (BiomeGenBase biomeGenBase : BiomeGenBase.getBiomeGenArray()) {
            if (biomeGenBase != null && biomeGenBase.biomeName.equals(name)) {
                return biomeGenBase;
            }
        }
        return null;
    }

    public enum Priority {
        HIGH,
        LOW;

        private static Priority fromString(String name) {
            switch (name) {
                case "high": return HIGH;
                case "low": return LOW;
            }
            return null;
        }
    }

    public static final class PriorityInfluence implements Comparable<PriorityInfluence> {
        private final List<BiomeInfluence.Influence> influences;
        private final BiomeGenBase biome;
        private final Priority priority;
        private final int filter;

        private String prerequisite;

        private PriorityInfluence(List<BiomeInfluence.Influence> influences, BiomeGenBase biome, Priority priority, int filter) {
            this.influences = influences;
            this.biome = biome;
            this.priority = priority;
            this.filter = filter;
        }

        private boolean canInfluence(World world, int x, int y, int z) {
            return BiomeInfluence.getInfluenceFor(world, influences, x, y, z) >= filter;
        }

        @Override
        public int compareTo(PriorityInfluence o) {
            return priority.compareTo(o.priority);
        }
    }
}
