package com.harleylizard.dynamicecosystem.chunk;

public interface NutrientsAccessor {

    int get(int x, int y, int z);

    void set(int x, int y, int z, int nutrients);

    void take(int x, int y, int z, int amount);

    void add(int x, int y, int z, int amount);
}
