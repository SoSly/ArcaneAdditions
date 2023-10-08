/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.renderers;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.sosly.arcaneadditions.entities.EntityRegistry;
import org.sosly.arcaneadditions.renderers.sorcery.AstralProjectionRenderer;
import org.sosly.arcaneadditions.renderers.sorcery.IceBlockRenderer;
import org.sosly.arcaneadditions.renderers.sorcery.SoulSearchersBeamRenderer;

;

public class RendererRegistry {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerEntityRenderers(FMLClientSetupEvent event) {
        EntityRenderers.register(EntityRegistry.ASTRAL_PROJECTION.get(), (context) -> new AstralProjectionRenderer(context, false));
        EntityRenderers.register(EntityRegistry.SOUL_SEARCHERS_BEAM.get(), SoulSearchersBeamRenderer::new);
        EntityRenderers.register(EntityRegistry.ICE_BLOCK.get(), IceBlockRenderer::new);
    }
}
