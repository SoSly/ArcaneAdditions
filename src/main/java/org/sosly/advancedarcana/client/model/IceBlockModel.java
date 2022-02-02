package org.sosly.advancedarcana.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.sosly.advancedarcana.client.entity.IceBlockEntity;
import org.sosly.advancedarcana.utils.RLoc;

public class IceBlockModel<T extends IceBlockEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(RLoc.create("iceblockmodel"), "main");
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
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        bone.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, 1, 1, 1, 1);
    }
}
