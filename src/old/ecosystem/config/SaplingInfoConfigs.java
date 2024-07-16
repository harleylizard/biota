package com.harleylizard.ecosystem.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SaplingInfoConfigs {
    private static final List<String> PATHS;

    static {
        List<String> list = new ArrayList<>();
        list.add("data/sapling_info/acacia");
        list.add("data/sapling_info/birch");
        list.add("data/sapling_info/dark_oak");
        list.add("data/sapling_info/jungle");
        list.add("data/sapling_info/oak");
        list.add("data/sapling_info/spruce");

        PATHS = Collections.unmodifiableList(list);
    }

    public static SaplingInfoConfigs createFromJson() {
        return null;
    }
}
