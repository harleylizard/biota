package com.harleylizard.ecosystem.world;

public interface EcosystemWorld {

    int getNourishment(int x, int y, int z);

    boolean removeNourishment(int x, int y, int z, int amount);

    void addNourishment(int x, int y, int z, int amount);
}
