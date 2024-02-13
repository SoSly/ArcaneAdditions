/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.renderers.sorcery;

import com.mna.api.ManaAndArtificeMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.sosly.arcaneadditions.entities.sorcery.SoulSearchersBeamEntity;
import org.sosly.arcaneadditions.renderers.RenderTypes;

import java.util.logging.Logger;

public class SoulSearchersBeamRenderer extends EntityRenderer<SoulSearchersBeamEntity> {
    private static int[] white = new int[]{255, 255, 255};

    public SoulSearchersBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(SoulSearchersBeamEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        LivingEntity source = entity.getSource(entity.level());
        LivingEntity target = entity.getTarget(entity.level());
        if (source != null && target != null) {
            Vec3 sourcePos = target.getBoundingBox().getCenter().subtract(entity.position());
            poseStack.pushPose();
            poseStack.translate(sourcePos.x, sourcePos.y, sourcePos.z);
            ManaAndArtificeMod.getWorldRenderUtils()
                .radiant(entity, poseStack, buffer, white, white, 128, 0.05F);
            poseStack.popPose();
            ManaAndArtificeMod.getWorldRenderUtils()
                .beam(entity.level(), partialTicks, poseStack, buffer, packedLight, entity.position(),
                    target.getBoundingBox().getCenter(), 1.0f, white, 0.1f, RenderTypes.SOULSEARCHERS_BEAM);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(SoulSearchersBeamEntity entity) { return null; }
}
