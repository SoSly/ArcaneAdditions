/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;
import org.sosly.arcaneadditions.entities.sorcery.IceBlockEntity;
import org.sosly.arcaneadditions.utils.RLoc;

public class IceBlockModel<T extends IceBlockEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(RLoc.create("ice_block"), "main");
    private final ModelPart bone;

    public IceBlockModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        CubeListBuilder clb = CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2, 8, 1, 11, 23, 16)
                .texOffs(0, 0).addBox(0, 19, 2, 13, 32, 13)
                .texOffs(0, 0).addBox(7,13,1,18,20,13)
                .texOffs(0, 0).addBox(7,18,1,18,29,13)
                .texOffs(0, 0).addBox(4, 0,-2,19,13,17)
                .texOffs(0, 0).addBox(-3,0,0,11,8,15);
        partdefinition.addOrReplaceChild("bone", clb, PartPose.offset(0, 0, 0));
        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void setupAnim(IceBlockEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        bone.xRot = pEntity.getXRot();
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack pPoseStack, @NotNull VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        bone.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, 1, 1, 1, 1);
    }
}
