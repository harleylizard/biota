package com.harleylizard.ecosystem;

public interface EcosystemGetter {

    int getNourishment(int x, int y, int z);

    boolean takeNourishment(int x, int y, int z, int amount);

    void addNourishment(int x, int y, int z, int amount);
}
