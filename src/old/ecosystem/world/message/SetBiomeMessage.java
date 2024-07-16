package com.harleylizard.ecosystem.world.message;

import com.harleylizard.ecosystem.DynamicEcosystem;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.biome.BiomeGenBase;

public final class SetBiomeMessage implements IMessage, IMessageHandler<SetBiomeMessage, IMessage> {
    private int biomeId;
    private int x;
    private int y;
    private int z;

    public SetBiomeMessage() {
    }

    public SetBiomeMessage(BiomeGenBase biome, int x, int y, int z) {
        biomeId = biome.biomeID;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        biomeId = byteBuf.readInt();
        x = byteBuf.readInt();
        y = byteBuf.readInt();
        z = byteBuf.readInt();
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        byteBuf.writeInt(biomeId);
        byteBuf.writeInt(x);
        byteBuf.writeInt(y);
        byteBuf.writeInt(z);
    }

    @Override
    public IMessage onMessage(SetBiomeMessage biomeMessage, MessageContext messageContext) {
        DynamicEcosystem.PROXY.setBiome(Minecraft.getMinecraft().theWorld, BiomeGenBase.getBiome(biomeMessage.biomeId), biomeMessage.x, biomeMessage.y, biomeMessage.z);
        return null;
    }
}
