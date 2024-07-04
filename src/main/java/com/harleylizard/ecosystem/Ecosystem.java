package com.harleylizard.ecosystem;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.Map;
import java.util.WeakHashMap;

public final class Ecosystem {
    private final static Map<Chunk, Ecosystem> MAP = new WeakHashMap<>();

    private final Palette[] palettes = new Palette[16];

    private Ecosystem(Chunk chunk) {
    }

    private Ecosystem() {
    }

    public int getNourishment(Chunk chunk, int x, int y, int z) {
        return getPaletteOrCreate(chunk, y).nourishment[Palette.indexOf(x, y, z)];
    }

    public int getNourishmentForClient(int x, int y, int z) {
        Palette palette = palettes[(y & 0xFF) >> 4];
        if (palette == null) {
            return 16;
        }
        return palette.nourishment[Palette.indexOf(x, y, z)];
    }

    public boolean takeNourishment(Chunk chunk, int x, int y, int z, int amount) {
        int[] nourishment = getPaletteOrCreate(chunk, y).nourishment;
        int i = Palette.indexOf(x, y, z);
        int j = nourishment[i];

        int taken = Math.max(j - amount, 0);
        nourishment[i] = taken;
        return taken > 0;
    }

    public void addNourishment(Chunk chunk, int x, int y, int z, int amount) {
        int[] nourishment = getPaletteOrCreate(chunk, y).nourishment;
        int i = Palette.indexOf(x, y, z);
        int j = nourishment[i];

        nourishment[i] = Math.min(j + amount, 16);
    }

    private Palette getPaletteOrCreate(Chunk chunk, int y) {
        int i = (y & 0xFF) >> 4;
        Palette palette = palettes[i];
        if (palette == null) {
            palette = new Palette();
            ExtendedBlockStorage storage = chunk.getBlockStorageArray()[y >> 4];
            for (int j = 0; j < 16; j++) for (int k = 0; k < 16; k++) for (int l = 0; l < 16; l++) {
                Block block = storage.getBlockByExtId(j, k, l);

                if (block instanceof BlockGrass || block instanceof BlockBush || block instanceof BlockLeaves) {
                    palette.nourishment[Palette.indexOf(j, k, l)] = 16;
                }
            }

            palettes[i] = palette;
            return palette;
        }
        return palette;
    }

    public static Ecosystem getOrCreate(Chunk chunk) {
        return MAP.computeIfAbsent(chunk, Ecosystem::new);
    }

    public static Ecosystem get(Chunk chunk) {
        return MAP.get(chunk);
    }

    public static void toClient(Chunk chunk, Ecosystem ecosystem, int x, int y, int z) {
        EcosystemMessage message = new EcosystemMessage();
        message.x = chunk.xPosition;
        message.y = chunk.zPosition;
        message.ecosystem = ecosystem;

        NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(chunk.worldObj.getWorldInfo().getDimension(), x, y, z, 50.0D);
        DynamicEcosystem.NETWORK_WRAPPER.sendToAllAround(message, targetPoint);
    }

    public static final class Palette {
        private final int[] nourishment = new int[16 * 16 * 16];

        private Palette() {
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
