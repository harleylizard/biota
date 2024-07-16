package com.harleylizard.ecosystem.world.message;

import com.harleylizard.ecosystem.world.ClientEcosystem;
import com.harleylizard.ecosystem.world.Ecosystem;
import com.harleylizard.ecosystem.world.MutableEcosystem;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.Arrays;

public final class EcosystemMessage implements IMessage, IMessageHandler<EcosystemMessage, IMessage> {
    private Ecosystem ecosystem;
    private int x;
    private int z;
    private int blockX;
    private int blockY;
    private int blockZ;


    public EcosystemMessage() {
    }

    public EcosystemMessage(MutableEcosystem ecosystem, int x, int z, int blockX, int blockY, int blockZ) {
        this.ecosystem = ecosystem;
        this.x = x;
        this.z = z;
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int[][] palettes = new int[16][16 * 16 * 16];
        for (int i = 0; i < 16; i++) {
            int b = buf.readByte();
            int[] palette = new int[16 * 16 * 16];
            if (b == 1) {
                for (int j = 0; j < palette.length; j++) {
                    palette[j] = buf.readByte();
                }
            } else {
                Arrays.fill(palette, 16);
            }
            palettes[i] = palette;
        }
        ecosystem = new ClientEcosystem(palettes);

        x = buf.readInt();
        z = buf.readInt();
        blockX = buf.readInt();
        blockY = buf.readInt();
        blockZ = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ((MutableEcosystem) ecosystem).toClient(buf);
        buf.writeInt(x);
        buf.writeInt(z);
        buf.writeInt(blockX);
        buf.writeInt(blockY);
        buf.writeInt(blockZ);
    }

    @Override
    public IMessage onMessage(EcosystemMessage message, MessageContext ctx) {
        int x = message.x;
        int z = message.z;
        World world = Minecraft.getMinecraft().theWorld;
        Chunk chunk = world.getChunkFromChunkCoords(x, z);

        ClientEcosystem.remap(chunk, (ClientEcosystem) message.ecosystem);

        world.markBlockForRenderUpdate(
                message.blockX,
                message.blockY,
                message.blockZ
        );
        return null;
    }
}
