/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.sosly.arcaneadditions.entities.IceBlockEntity;
import org.sosly.arcaneadditions.model.IceBlockModel;

public class IceBlockRenderer extends EntityRenderer<IceBlockEntity> {
    public static final ResourceLocation ICE_TEXTURE = new ResourceLocation("minecraft", "textures/block/ice.png");
    private final IceBlockModel<IceBlockEntity> model;

    public IceBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new IceBlockModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(IceBlockModel.LAYER_LOCATION));
    }

    @Override
    @NotNull
    public ResourceLocation getTextureLocation(@NotNull IceBlockEntity pEntity) {
        return ICE_TEXTURE;
    }

    @Override
    public void render(@NotNull IceBlockEntity pEntity, float pEntityYaw, float pPartialTicks, @NotNull PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        RenderType rendertype = RenderType.entityTranslucent(ICE_TEXTURE);
        VertexConsumer consumer = pBuffer.getBuffer(rendertype);

        model.renderToBuffer(pMatrixStack, consumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }
}
