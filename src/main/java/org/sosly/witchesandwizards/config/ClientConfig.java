package org.sosly.witchesandwizards.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ClientConfig {
    public static ForgeConfigSpec CONFIG;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        ClientConfig.CONFIG = builder.build();
    }
}