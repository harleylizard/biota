package com.harleylizard.ecosystem.config;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.io.*;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.util.Objects.requireNonNull;

public final class InfluenceConfigs {
    private static final List<String> PATHS;

    static {
        List<String> list = new ArrayList<>();
        list.add("config/influence/swampland.json");
        list.add("config/influence/taiga.json");
        list.add("config/influence/sunflower_plains.json");
        list.add("config/influence/jungle.json");
        list.add("config/influence/birch_forest.json");
        list.add("config/influence/forest.json");
        list.add("config/influence/plains.json");
        list.add("config/influence/magical_forest.json");
        list.add("config/influence/maple_woods.json");

        PATHS = Collections.unmodifiableList(list);
    }

    private final List<Influence> influences;
    private final Map<Influence, Set<Influence>> hierarchy;

    private InfluenceConfigs(List<Influence> influences, Map<Influence, Set<Influence>> hierarchy) {
        this.influences = influences;
        this.hierarchy = hierarchy;
    }

    public Influence getInfluence(World world, int x, int y, int z) {
        for (Influence influence : influences) {
            if (influence.getPrerequisiteBiome() == null && influence.canInfluence(world, x, y, z)) {
                if (hierarchy.containsKey(influence)) {
                    for (Influence child : hierarchy.get(influence)) {
                        if (child.canInfluence(world, x, y, z)) {
                            return child;
                        }
                    }
                }
                return influence;
            }
        }
        return null;
    }

    public static InfluenceConfigs createFromJson() {
        try {
            Path path = Paths.get("config", "dynamic-ecosystems");

            List<Influence> list = new ArrayList<>();
            for (String jsonFile : PATHS) {
                validateInfluence(path, jsonFile);
            }
            path = path.resolve("influence");
            if (Files.isDirectory(path)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, path1 -> path1.toString().endsWith(".json"))) {
                    for (Path path1 : stream) {
                        try (BufferedReader reader = Files.newBufferedReader(path1)) {
                            Influence influence = Influence.GSON.fromJson(reader, Influence.class);
                            if (influence != null) {
                                list.add(influence);
                            }
                        }
                    }
                }
            }
            list.sort(Comparable::compareTo);

            Map<Influence, Set<Influence>> map = new HashMap<>();
            for (Influence influence : list) {
                BiomeGenBase prerequisite = influence.getPrerequisiteBiome();
                if (prerequisite != null) {

                    Influence parent = null;
                    for (Influence key : list) {
                        if (influence == key) {
                            continue;
                        }
                        BiomeGenBase biome = key.getBiome();
                        if (biome != null && biome.biomeName.equals(prerequisite.biomeName)) {
                            parent = key;
                            break;
                        }
                    }
                    if (parent != null) {
                        map.computeIfAbsent(parent, ignored -> new HashSet<>()).add(influence);
                    }
                }
            }
            return new InfluenceConfigs(Collections.unmodifiableList(list), Collections.unmodifiableMap(map));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void validateInfluence(Path path, String jsonFile) throws IOException {
        path = path.resolve(jsonFile.substring(jsonFile.indexOf("/") + 1));
        if (!Files.exists(path)) {
            Path parent = path.getParent();
            if (!Files.isDirectory(path)) {
                Files.createDirectories(parent);
            }

            URL url = requireNonNull(InfluenceConfigs.class.getClassLoader().getResource(jsonFile));
            try (InputStream stream = new BufferedInputStream(url.openStream()); OutputStream out = Files.newOutputStream(path)) {
                int byteRead;
                while ((byteRead = stream.read()) != -1) {
                    out.write(byteRead);
                }
            }
        }
    }
}
