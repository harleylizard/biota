package com.harleylizard.ecosystem.message;

import com.harleylizard.ecosystem.DynamicEcosystem;
import com.harleylizard.ecosystem.world.BlockPos;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public final class SetBiomeMessage implements IMessage, IMessageHandler<SetBiomeMessage, IMessage> {
    private int biomeId;
    private int x;
    private int z;

    public SetBiomeMessage() {
    }

    public SetBiomeMessage(BiomeGenBase biomeGenBase, BlockPos blockPos) {
        biomeId = biomeGenBase.biomeID;
        x = blockPos.getX();
        z = blockPos.getZ();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        biomeId = buf.readInt();
        x = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(biomeId);
        buf.writeInt(x);
        buf.writeInt(z);
    }

    @Override
    public IMessage onMessage(SetBiomeMessage message, MessageContext ctx) {
        World world = Minecraft.getMinecraft().theWorld;
        int x = message.x;
        int z = message.z;
        int y = world.getHeightValue(x, z);
        DynamicEcosystem.PROXY.setBiome(world, BiomeGenBase.getBiome(message.biomeId), new BlockPos(x, y, z));
        return null;
    }
}
