package org.sosly.arcaneadditions.client.renderer;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.sosly.arcaneadditions.client.entity.EntityRegistry;
import org.sosly.arcaneadditions.client.renderer.entity.IceBlockRenderer;

public class RendererRegistry {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerEntityRenderers(FMLClientSetupEvent event) {
        EntityRenderers.register(EntityRegistry.ICE_BLOCK.get(), IceBlockRenderer::new);
    }
}
