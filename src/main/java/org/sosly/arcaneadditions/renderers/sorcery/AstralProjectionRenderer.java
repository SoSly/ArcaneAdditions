//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.sosly.arcaneadditions.renderers.sorcery;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import org.sosly.arcaneadditions.entities.sorcery.AstralProjectionEntity;

public class AstralProjectionRenderer extends LivingEntityRenderer<AstralProjectionEntity, PlayerModel<AstralProjectionEntity>> {
    private static final ResourceLocation DEFAULT = new ResourceLocation("");

    public AstralProjectionRenderer(EntityRendererProvider.Context context, boolean slim) {
        super(context, null, 0.0F);
        model = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), slim);
    }

    public ResourceLocation getTextureLocation(AstralProjectionEntity entity) {
        return entity.getPlayer() != null && entity.getPlayer() instanceof AbstractClientPlayer ?
                ((AbstractClientPlayer)entity.getPlayer()).getSkinTextureLocation() : DEFAULT;
    }

    public boolean shouldShowName(AstralProjectionEntity entity) {
        return false;
    }
}
