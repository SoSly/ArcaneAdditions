package org.sosly.witchesandwizards.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.sosly.witchesandwizards.WitchesAndWizards;
import org.sosly.witchesandwizards.world.menu.FlowerCrownMenu;

public class FlowerCrownScreen extends AbstractContainerScreen<FlowerCrownMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WitchesAndWizards.MOD_ID, "textures/gui/flower_crown.png");

    public FlowerCrownScreen(FlowerCrownMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);

        this.leftPos = 0;
        this.topPos = 0;
        this.imageWidth = 176;
        this.imageHeight = 132;
        this.inventoryLabelY = 40;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(stack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}
