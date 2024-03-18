package org.sosly.arcaneadditions.models;

import com.mna.api.tools.RLoc;
import com.mna.blocks.tileentities.models.WizardLabModel;
import com.mna.blocks.tileentities.wizard_lab.WizardLabTile;
import com.mna.items.sorcery.ItemSpell;
import com.mna.tools.render.ModelUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.sosly.arcaneadditions.blocks.tileentities.ScribesBenchTile;

public class ScribesBenchModel extends WizardLabModel<ScribesBenchTile> {
    private static final ResourceLocation animFile = new ResourceLocation("mna", "animations/block/none.animation.json");
    private static final ResourceLocation texFile = new ResourceLocation("mna", "textures/block/material/mandala_circle.png");
    private static final ResourceLocation modelFile = new ResourceLocation("arcaneadditions", "geo/block/scribes_desk.geo.json");
    public static final ResourceLocation ink = new ResourceLocation("mna", "block/wizard_lab/special/transcription_table_ink");
    public static final ResourceLocation lapis = new ResourceLocation("mna", "block/wizard_lab/special/transcription_table_lapis");
    public static final ResourceLocation source = new ResourceLocation("arcaneadditions", "block/scribes_bench_source");
    public static final ResourceLocation target = new ResourceLocation("arcaneadditions", "block/scribes_bench_target");

    public ScribesBenchModel() {
        this.boneOverrides.add(new WizardLabModel.GeoBoneRenderer(0, "STATICS", ink));
        this.boneOverrides.add(new WizardLabModel.GeoBoneRenderer(1, "STATICS", lapis));
        this.boneOverrides.add(new WizardLabModel.GeoBoneRenderer(2, "SPELL_SOURCE", source, (pose) -> {
            pose.translate(0, -0.06, -0.4);
        }));
        this.boneOverrides.add(new WizardLabModel.GeoBoneRenderer(3, "SPELL_TARGET", target, (pose) -> {
            pose.translate(0, -0.06, -0.4);
        }));
        this.boneOverrides.add(new WizardLabModel.GeoBoneRenderer(3, "SPELL_TARGET_WRITTEN", source, (pose) -> {
            pose.translate(-0.375, -0.06, -0.4);
        }));
    }

    @Override
    public void renderBoneAdditions(WizardLabTile tile, String bone, PoseStack stack, MultiBufferSource bufferIn, RenderType renderType, int packedLightIn, int packedOverlayIn) {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();
        this.boneOverrides.stream()
            .filter((b) -> b.bone.equals(bone) && (b.slot == -1 || tile.hasStack(b.slot)))
            .filter((b) -> {
                if (b.slot != ScribesBenchTile.SLOT_VELLUM) {
                    return true;
                }

                return b.model.equals(target) != (tile.getItem(b.slot).getItem() instanceof ItemSpell);
            })
            .forEach(b -> {
                if (b.poseAdjuster != null) {
                    stack.pushPose();
                    b.poseAdjuster.accept(stack);
                }

                if (b.model != null) {
                    ModelUtils.renderModel(bufferIn.getBuffer(RenderType.solid()), tile.getLevel(), tile.getBlockPos(), tile.getBlockState(), b.model, stack, packedLightIn, packedOverlayIn);
                } else {
                    ItemStack stackToRender = !b.stack.isEmpty() ? b.stack : tile.getItem(b.slot);
                    itemRenderer.renderStatic(stackToRender, ItemDisplayContext.GROUND, packedLightIn, packedOverlayIn, stack, bufferIn, mc.level, 0);
                }

                if (b.poseAdjuster != null) {
                    stack.popPose();
                }
            });
    }

    public ResourceLocation getAnimationResource(ScribesBenchTile arg0) {
        return animFile;
    }

    public ResourceLocation getModelResource(ScribesBenchTile arg0) {
        return modelFile;
    }

    public ResourceLocation getTextureResource(ScribesBenchTile arg0) {
        return texFile;
    }
}
