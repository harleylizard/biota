package com.harleylizard.dynamicecosystem.message;

import com.harleylizard.dynamicecosystem.chunk.ClientNutrients;
import com.harleylizard.dynamicecosystem.chunk.MutableNutrients;
import com.harleylizard.dynamicecosystem.chunk.Nutrients;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;

public final class UpdateNutrientsMessage implements IMessage, IMessageHandler<UpdateNutrientsMessage, IMessage> {
    private Nutrients nutrients;
    private int chunkX;
    private int chunkZ;

    public UpdateNutrientsMessage() {
    }

    public UpdateNutrientsMessage(MutableNutrients nutrients, Chunk chunk) {
        this.nutrients = nutrients;
        chunkX = chunk.xPosition;
        chunkZ = chunk.zPosition;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        nutrients = ClientNutrients.readFrom(buf);
        chunkX = buf.readInt();
        chunkZ = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ((MutableNutrients) nutrients).writeTo(buf);
        buf.writeInt(chunkX);
        buf.writeInt(chunkZ);
    }

    @Override
    public IMessage onMessage(UpdateNutrientsMessage message, MessageContext ctx) {
        int chunkX = message.chunkX;
        int chunkZ = message.chunkZ;
        Chunk chunk = Minecraft.getMinecraft().theWorld.getChunkFromChunkCoords(chunkX, chunkZ);
        ClientNutrients.MAP.put(chunk, (ClientNutrients) message.nutrients);
        return null;
    }
}
