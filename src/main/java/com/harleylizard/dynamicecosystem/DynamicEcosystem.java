package com.harleylizard.dynamicecosystem;

import com.harleylizard.dynamicecosystem.message.SetBiomeMessage;
import com.harleylizard.dynamicecosystem.message.UpdateNutrientsMessage;
import com.harleylizard.dynamicecosystem.proxy.Proxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;

@Mod(
        modid = DynamicEcosystem.MOD_ID,
        name = DynamicEcosystem.MOD_NAME,
        version = DynamicEcosystem.MOD_VERSION,
        acceptedMinecraftVersions = "1.7.10",
        dependencies = "required-after:unimixins@[0.1.17,)"
)
public final class DynamicEcosystem {
    public static final String MOD_ID = "dynamic-ecosystem";
    public static final String MOD_NAME = "Dynamic Ecosystem";
    public static final String MOD_VERSION = "1.0-SNAPSHOT";

    @SidedProxy(
            clientSide = "com.harleylizard.dynamicecosystem.proxy.ClientProxy",
            serverSide = "com.harleylizard.dynamicecosystem.proxy.ServerProxy"
    )
    public static Proxy PROXY;

    @Mod.Instance
    public static DynamicEcosystem INSTANCE;

    private final SimpleNetworkWrapper wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        int id = 0;
        wrapper.registerMessage(UpdateNutrientsMessage.class, UpdateNutrientsMessage.class, id++, Side.CLIENT);
        wrapper.registerMessage(SetBiomeMessage.class, SetBiomeMessage.class, id++, Side.CLIENT);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public SimpleNetworkWrapper getWrapper() {
        return wrapper;
    }
}
