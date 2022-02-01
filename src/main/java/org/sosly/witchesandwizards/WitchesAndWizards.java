package org.sosly.witchesandwizards;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.sosly.witchesandwizards.config.ClientConfig;
import org.sosly.witchesandwizards.config.ConfigLoader;
import org.sosly.witchesandwizards.effects.EffectRegistry;
import org.sosly.witchesandwizards.events.EventRegistry;

@Mod(WitchesAndWizards.MOD_ID)
public class WitchesAndWizards {
    public static final String MOD_ID = "wnw";

    public WitchesAndWizards() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CONFIG);
        ConfigLoader.loadConfig(ClientConfig.CONFIG, FMLPaths.CONFIGDIR.get().resolve("wnw-client.toml"));

        // Initialize Registries
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        EffectRegistry.EFFECTS.register(modBus);
        EventRegistry.register();
    }
}