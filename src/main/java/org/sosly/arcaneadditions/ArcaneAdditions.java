/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions;

import com.mna.api.guidebook.RegisterGuidebooksEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sosly.arcaneadditions.compats.CompatRegistry;
import org.sosly.arcaneadditions.configs.Config;
import org.sosly.arcaneadditions.effects.EffectRegistry;
import org.sosly.arcaneadditions.entities.EntityRegistry;
import org.sosly.arcaneadditions.items.ItemRegistry;
import org.sosly.arcaneadditions.menus.MenuRegistry;
import org.sosly.arcaneadditions.renderers.RendererRegistry;
import org.sosly.arcaneadditions.utils.ClientProxy;
import org.sosly.arcaneadditions.utils.ISidedProxy;
import org.sosly.arcaneadditions.utils.RLoc;

@Mod(ArcaneAdditions.MOD_ID)
public class ArcaneAdditions {
    public static final String MOD_ID = "arcaneadditions";
    public static final Logger LOGGER = LogManager.getLogger(ArcaneAdditions.class);
    public static ArcaneAdditions instance;
    public ISidedProxy proxy;

    public ArcaneAdditions() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
        instance = this;

        // Initialize Registries
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        EffectRegistry.EFFECTS.register(modbus);
        EntityRegistry.ENTITY_TYPES.register(modbus);
        ItemRegistry.ITEMS.register(modbus);
        MenuRegistry.MENUS.register(modbus);
        MinecraftForge.EVENT_BUS.register(this);
        modbus.addListener(ArcaneAdditions::setupCommon);

        if (Dist.CLIENT.isClient()) {
            modbus.register(RendererRegistry.class);
            this.proxy = new ClientProxy();
        }
    }

    @SubscribeEvent
    public void onRegisterGuidebooks(RegisterGuidebooksEvent event) {
        event.getRegistry().addGuidebookPath(RLoc.create("guide"));
        org.sosly.arcaneadditions.ArcaneAdditions.LOGGER.info("arcaneadditions: guide registered");
    }

    private static void setupCommon(FMLCommonSetupEvent event) {
        CompatRegistry.registerCompats();
    }
}