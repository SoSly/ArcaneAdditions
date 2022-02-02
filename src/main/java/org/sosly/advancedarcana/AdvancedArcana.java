package org.sosly.advancedarcana;

import com.mna.api.guidebook.RegisterGuidebooksEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sosly.advancedarcana.client.entity.EntityRegistry;
import org.sosly.advancedarcana.client.renderer.RendererRegistry;
import org.sosly.advancedarcana.config.SpellsConfig;
import org.sosly.advancedarcana.config.ConfigLoader;
import org.sosly.advancedarcana.effects.EffectRegistry;
import org.sosly.advancedarcana.events.SpellEventRegistry;
import org.sosly.advancedarcana.utils.RLoc;

@Mod(AdvancedArcana.MOD_ID)
public class AdvancedArcana {
    public static final String MOD_ID = "advancedarcana";
    public static final Logger LOGGER = LogManager.getLogger();

    public AdvancedArcana() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SpellsConfig.CONFIG);
        ConfigLoader.loadConfig(SpellsConfig.CONFIG, FMLPaths.CONFIGDIR.get().resolve("advancedarcana-spells.toml"));

        // Initialize Registries
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        EffectRegistry.EFFECTS.register(modBus);
        EntityRegistry.ENTITY_TYPES.register(modBus);

        MinecraftForge.EVENT_BUS.register(SpellEventRegistry.class);
        MinecraftForge.EVENT_BUS.register(this);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> {
            return () -> {
                modBus.register(RendererRegistry.class);
            };
        });
    }

    @SubscribeEvent
    public void onRegisterGuidebooks(RegisterGuidebooksEvent event) {
        event.getRegistry().addGuidebookPath(RLoc.create("guide"));
        AdvancedArcana.LOGGER.info("AdvancedArcana: guide registered");
    }
}