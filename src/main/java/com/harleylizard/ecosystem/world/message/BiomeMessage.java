package com.harleylizard.ecosystem.world.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public final class BiomeMessage implements IMessage, IMessageHandler<BiomeMessage, IMessage> {
    @Override
    public void fromBytes(ByteBuf byteBuf) {

    }

    @Override
    public void toBytes(ByteBuf byteBuf) {

    }

    @Override
    public IMessage onMessage(BiomeMessage biomeMessage, MessageContext messageContext) {
        return null;
    }
}
