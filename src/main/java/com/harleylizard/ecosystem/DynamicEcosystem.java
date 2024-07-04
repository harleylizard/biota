package com.harleylizard.ecosystem;

import com.harleylizard.ecosystem.proxy.Proxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = DynamicEcosystem.MOD_ID, version = DynamicEcosystem.VERSION, name = DynamicEcosystem.NAME, dependencies = "required-after:unimixins@[0.1.17,)")
public final class DynamicEcosystem {
    public static final String MOD_ID = "dynamic-ecosystem";
    public static final String VERSION = "1.0-SNAPSHOT";
    public static final String NAME = "Dynamic Ecosystem";

    public static final SimpleNetworkWrapper NETWORK_WRAPPER = new SimpleNetworkWrapper(MOD_ID);

    @SidedProxy(
            clientSide = "com.harleylizard.ecosystem.proxy.ClientProxy",
            serverSide = "com.harleylizard.ecosystem.proxy.CommonProxy"
    )
    public static Proxy PROXY;

    @Mod.Instance
    public static DynamicEcosystem INSTANCE;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NETWORK_WRAPPER.registerMessage(Ecosystem.EcosystemMessage.class, Ecosystem.EcosystemMessage.class, 0, Side.CLIENT);
    }
}
