package com.harleylizard.ecosystem;

import com.harleylizard.ecosystem.proxy.Proxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

@Mod(modid = DynamicEcosystem.MOD_ID, version = DynamicEcosystem.VERSION, name = DynamicEcosystem.NAME, dependencies = "required-after:unimixins@[0.1.17,)")
public final class DynamicEcosystem {
    public static final String MOD_ID = "dynamic-ecosystem";
    public static final String VERSION = "1.0-SNAPSHOT";
    public static final String NAME = "Dynamic Ecosystem";

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
        NETWORK_WRAPPER.registerMessage(Ecosystem.EcosystemMessage.class, Ecosystem.EcosystemMessage.class, 0, Side.CLIENT);

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
            Ecosystem ecosystem = Ecosystem.get(chunk);
            if (ecosystem != null) {
                ecosystem.takeNourishment(chunk, x, y, z, 16);
                Ecosystem.toClient(chunk, ecosystem, x, y, z);
            }
        }
    }

    public static int mix(int x, int  y, float t) {
        int xR = (x >> 16) & 0xFF;
        int xG = (x >> 8) & 0xFF;
        int xB = (x >> 0) & 0xFF;
        int yR = (y >> 16) & 0xFF;
        int yG = (y >> 8) & 0xFF;
        int yB = (y >> 0) & 0xFF;

        int zR = mixInt(xR, yR, t);
        int zG = mixInt(xG, yG, t);
        int zB = mixInt(xB, yB, t);
        return zR << 16 | zG << 8 | zB;
    }

    private static int mixInt(float x, float y, float t) {
        return (int) Math.floor(x + t * (y - x)) & 0xFF;
    }
}
