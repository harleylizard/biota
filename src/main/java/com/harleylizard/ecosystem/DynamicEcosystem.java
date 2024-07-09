package com.harleylizard.ecosystem;

import com.harleylizard.ecosystem.config.InfluenceConfigs;
import com.harleylizard.ecosystem.config.SaplingInfoConfigs;
import com.harleylizard.ecosystem.proxy.Proxy;
import com.harleylizard.ecosystem.world.MutableEcosystem;
import com.harleylizard.ecosystem.world.message.EcosystemMessage;
import com.harleylizard.ecosystem.world.message.SetBiomeMessage;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLeaves;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkDataEvent;

import java.util.function.Supplier;

@Mod(modid = DynamicEcosystem.MOD_ID, version = DynamicEcosystem.VERSION, name = DynamicEcosystem.NAME, dependencies = "required-after:unimixins@[0.1.17,)")
public final class DynamicEcosystem {
    public static final String MOD_ID = "dynamic-ecosystem";
    public static final String VERSION = "1.0-SNAPSHOT";
    public static final String NAME = "Dynamic Ecosystem";

    public static final Supplier<InfluenceConfigs> INFLUENCE_CONFIGS = MemorableSupplier.of(InfluenceConfigs::createFromJson);
    public static final Supplier<SaplingInfoConfigs> SAPLING_INFO_CONFIGS = MemorableSupplier.of(SaplingInfoConfigs::createFromJson);

    public static SimpleNetworkWrapper NETWORK_WRAPPER;

    @SidedProxy(
            clientSide = "com.harleylizard.ecosystem.proxy.ClientProxy",
            serverSide = "com.harleylizard.ecosystem.proxy.CommonProxy"
    )
    public static Proxy PROXY;

    @Mod.Instance
    public static DynamicEcosystem INSTANCE;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);

        int id = 0;
        NETWORK_WRAPPER.registerMessage(EcosystemMessage.class, EcosystemMessage.class, id++, Side.CLIENT);
        NETWORK_WRAPPER.registerMessage(SetBiomeMessage.class, SetBiomeMessage.class, id++, Side.CLIENT);

        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void breakBlock(BlockEvent.BreakEvent event) {
        World world = event.world;
        if (!world.isRemote) {
            int x = event.x;
            int y = event.y;
            int z = event.z;
            Chunk chunk = world.getChunkFromBlockCoords(x, z);
            MutableEcosystem ecosystem = MutableEcosystem.get(chunk, y);
            ecosystem.removeNourishment(x, y, z, 16);
            DynamicEcosystemHelper.sendEcosystem(world, x, y, z);
        }
    }

    @SubscribeEvent
    public void loadChunk(ChunkDataEvent.Load event) {
        if (!event.world.isRemote) {
            NBTTagCompound data = event.getData();
            if (data.hasKey("Ecosystem", Constants.NBT.TAG_COMPOUND)) {
                Chunk chunk = event.getChunk();
                MutableEcosystem ecosystem = MutableEcosystem.load(chunk, data.getCompoundTag("Ecosystem"));
                chunk.setChunkModified();
            }
        }
    }

    @SubscribeEvent
    public void saveChunk(ChunkDataEvent.Save event) {
        if (!event.world.isRemote) {
            Chunk chunk = event.getChunk();
            MutableEcosystem ecosystem = MutableEcosystem.maybeGet(chunk);
            if (ecosystem != null) {
                event.getData().setTag("Ecosystem", ecosystem.getCompoundTag());
            }
        }
    }

    public static boolean isPlant(Block block) {
        return block instanceof BlockGrass || block instanceof BlockLeaves || block instanceof BlockBush;
    }
}
