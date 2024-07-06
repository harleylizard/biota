package com.harleylizard.ecosystem.world.message;

import com.harleylizard.ecosystem.world.ClientEcosystem;
import com.harleylizard.ecosystem.world.Ecosystem;
import com.harleylizard.ecosystem.world.MutableEcosystem;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;

import java.util.Arrays;

public final class EcosystemMessage implements IMessage, IMessageHandler<EcosystemMessage, IMessage> {
    private int x;
    private int z;
    private Ecosystem ecosystem;

    public EcosystemMessage() {
    }

    public EcosystemMessage(MutableEcosystem ecosystem, int x, int z) {
        this.x = x;
        this.z = z;
        this.ecosystem = ecosystem;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        z = buf.readInt();

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
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(z);
        ((MutableEcosystem) ecosystem).toClient(buf);
    }

    @Override
    public IMessage onMessage(EcosystemMessage message, MessageContext ctx) {
        int x = message.x;
        int z = message.z;
        Chunk chunk = Minecraft.getMinecraft().theWorld.getChunkFromChunkCoords(x, z);

        ClientEcosystem.remap(chunk, (ClientEcosystem) message.ecosystem);
        chunk.setChunkModified();
        chunk.sendUpdates = true;
        return null;
    }
}
