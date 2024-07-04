package com.harleylizard.ecosystem;

import net.minecraft.world.chunk.Chunk;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ChunkQueue {
    private final ConcurrentLinkedQueue<Entry> queue = new ConcurrentLinkedQueue<>();
    private final Map<Chunk, Entry> map = new WeakHashMap<>();

    public void push(Chunk chunk, Ecosystem ecosystem, int x, int y, int z) {
        if (!map.containsKey(chunk)) {
            Entry entry = new Entry(chunk, ecosystem, x, y, z);
            queue.offer(entry);
            map.put(chunk, entry);
        }
    }

    public void poll() {
        if (!queue.isEmpty()) {
            Entry entry = queue.poll();
            map.remove(entry.chunk);
            Ecosystem.toClient(entry.chunk, entry.ecosystem, entry.x, entry.y, entry.z);
        }
    }

    private static final class Entry {
        private final Chunk chunk;
        private final Ecosystem ecosystem;
        private final int x;
        private final int y;
        private final int z;

        private Entry(Chunk chunk, Ecosystem ecosystem, int x, int y, int z) {
            this.chunk = chunk;
            this.ecosystem = ecosystem;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
