package com.harleylizard.ecosystem;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;

import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;

public final class Ecosystem {
    private final static Map<Chunk, Ecosystem> MAP = new WeakHashMap<>();

    private final Palette[] palettes = new Palette[16];

    private Ecosystem() {
    }

    public int getNourishment(int x, int y, int z) {
        return getPalette(y).getNourishment(x, y, z);
    }

    private Palette getPalette(int y) {
        int i = (y & 0xFF) >> 4;
        Palette palette = palettes[i];
        if (palette == null) {
            palette = new Palette();
            palettes[i] = palette;
            return palette;
        }
        return palette;
    }

    public static Ecosystem getOrCreate(Chunk chunk) {
        return MAP.computeIfAbsent(chunk, c -> new Ecosystem());
    }

    public static Ecosystem get(Chunk chunk) {
        return MAP.get(chunk);
    }

    public static void toClient(Chunk chunk, Ecosystem ecosystem) {
        int x = chunk.xPosition;
        int z = chunk.zPosition;

        EcosystemMessage message = new EcosystemMessage();
        message.x = x;
        message.y = z;
        message.ecosystem = ecosystem;
        DynamicEcosystem.NETWORK_WRAPPER.sendToAll(message);
    }

    public static final class Palette {
        private final int[] nourishment = new int[16 * 16 * 16];

        private Palette() {
            Arrays.fill(nourishment, -1);
        }

        private int getNourishment(int x, int y, int z) {
            return nourishment[indexOf(x, y, z)];
        }

        private static int indexOf(int x, int y, int z) {
            x = Math.floorMod(x, 16);
            y = Math.floorMod(y, 16);
            z = Math.floorMod(z, 16);
            return (z * 16 * 16) + (y * 16) + x;
        }
    }

    public static final class EcosystemMessage implements IMessage, IMessageHandler<EcosystemMessage, IMessage> {
        private Ecosystem ecosystem;
        private int x;
        private int y;

        public EcosystemMessage() {
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            x = buf.readInt();
            y = buf.readInt();
            Ecosystem copy = new Ecosystem();
            for (int i = 0; i < 16; i++) {
                int b = buf.readByte();
                if (b == 1) {
                    Palette palette = new Palette();
                    for (int j = 0; j < 16 * 16 * 16; j++) {
                        palette.nourishment[j] = buf.readInt();
                    }
                    copy.palettes[i] = palette;
                }
            }
            ecosystem = copy;
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(x);
            buf.writeInt(y);
            for (int i = 0; i < 16; i++) {
                Palette palette = ecosystem.palettes[i];
                if (palette == null) {
                    buf.writeByte(0);
                    continue;
                }
                buf.writeByte(1);
                for (int j = 0; j < 16 * 16 * 16; j++) {
                    buf.writeInt(palette.nourishment[j]);
                }
            }
        }

        @Override
        public IMessage onMessage(EcosystemMessage message, MessageContext ctx) {
            int x = message.x;
            int y = message.y;
            Ecosystem ecosystem = message.ecosystem;

            Chunk chunk = Minecraft.getMinecraft().theWorld.getChunkFromChunkCoords(x, y);
            MAP.put(chunk, ecosystem);
            return null;
        }
    }
}
