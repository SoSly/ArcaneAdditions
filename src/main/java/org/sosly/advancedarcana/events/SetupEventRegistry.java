package org.sosly.advancedarcana.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.sosly.advancedarcana.AdvancedArcana;
import org.sosly.advancedarcana.client.model.IceBlockModel;

@Mod.EventBusSubscriber(modid = AdvancedArcana.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SetupEventRegistry {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(IceBlockModel.LAYER_LOCATION, IceBlockModel::createBodyLayer);
    }
}
