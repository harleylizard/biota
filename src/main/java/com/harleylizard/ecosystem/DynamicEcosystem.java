package com.harleylizard.ecosystem;

import com.harleylizard.ecosystem.proxy.Proxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;

@Mod(modid = DynamicEcosystem.MOD_ID, version = DynamicEcosystem.VERSION, name = DynamicEcosystem.NAME)
public final class DynamicEcosystem {
    public static final String MOD_ID = "dynamic-ecosystem";
    public static final String VERSION = "1.0-SNAPSHOT";
    public static final String NAME = "Dynamic Ecosystem";

    @SidedProxy(
            clientSide = "com.harleylizard.ecosystem.proxy.ClientProxy",
            serverSide = "com.harleylizard.ecosystem.proxy.CommonProxy"
    )
    public static Proxy PROXY;

    @Mod.Instance
    public static DynamicEcosystem INSTANCE;


}
