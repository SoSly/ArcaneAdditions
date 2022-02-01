package org.sosly.witchesandwizards.client.renderer;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.sosly.witchesandwizards.client.entity.EntityRegistry;
import org.sosly.witchesandwizards.client.renderer.entity.IceBlockRenderer;

public class RendererRegistry {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerEntityRenderers(FMLClientSetupEvent event) {
        EntityRenderers.register((EntityType) EntityRegistry.ICE_BLOCK.get(), IceBlockRenderer::new);
    }
}
