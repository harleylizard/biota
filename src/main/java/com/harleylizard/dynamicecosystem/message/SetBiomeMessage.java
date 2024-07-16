package com.harleylizard.dynamicecosystem.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.biome.BiomeGenBase;

public final class SetBiomeMessage implements IMessage, IMessageHandler<SetBiomeMessage, IMessage> {
    private int biomeId;
    private int x;
    private int y;
    private int z;

    public SetBiomeMessage() {
    }

    public SetBiomeMessage(BiomeGenBase base, int x, int y, int z) {
        biomeId = base.biomeID;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        biomeId = buf.readInt();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(biomeId);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public IMessage onMessage(SetBiomeMessage message, MessageContext ctx) {
        return null;
    }
}
