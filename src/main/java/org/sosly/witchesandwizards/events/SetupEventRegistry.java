package org.sosly.witchesandwizards.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.sosly.witchesandwizards.WitchesAndWizards;
import org.sosly.witchesandwizards.client.model.IceBlockModel;

@Mod.EventBusSubscriber(modid = WitchesAndWizards.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SetupEventRegistry {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(IceBlockModel.LAYER_LOCATION, IceBlockModel::createBodyLayer);
    }
}
