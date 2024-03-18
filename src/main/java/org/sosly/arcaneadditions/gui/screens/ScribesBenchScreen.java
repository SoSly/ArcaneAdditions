package org.sosly.arcaneadditions.gui.screens;

import com.mna.ManaAndArtifice;
import com.mna.gui.block.SimpleWizardLabDeskGui;
import com.mna.tools.math.MathUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.sosly.arcaneadditions.gui.menus.ScribesBenchMenu;
import org.sosly.arcaneadditions.utils.RLoc;

public class ScribesBenchScreen extends SimpleWizardLabDeskGui<ScribesBenchMenu> {
    public ScribesBenchScreen(ScribesBenchMenu screenContainer, Inventory inv, Component title) {
        super(screenContainer, inv, Component.literal(""));
        this.imageWidth = 176;
        this.imageHeight = 148;
    }

    @Override
    protected ResourceLocation texture() {
        return RLoc.create("textures/gui/scribes_bench.png");
    }

    protected void renderBg(GuiGraphics pGuiGraphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(pGuiGraphics, partialTicks, mouseX, mouseY);
        if ((this.menu).isActive()) {
            float pct = (this.menu).getProgress();
            pGuiGraphics.blit(this.texture(), this.leftPos + 84, this.topPos + 14, 225, 39, (int)(31.0F * pct), 31);
        }

        Player player = ManaAndArtifice.instance.proxy.getClientPlayer();
        float xpPct = MathUtils.clamp01(player.isCreative() ? 1.0F : (float)player.totalExperience / (float)(this.menu).getXPCost());
        int VCoord = xpPct < 1.0F ? 5 : 0;
        pGuiGraphics.blit(this.texture(), this.leftPos + 112, this.topPos + 52, 220, VCoord, (int)(36.0F * xpPct), 5);
    }

    protected void renderLabels(GuiGraphics pGuiGraphics, int x, int y) {
        ItemStack stack = new ItemStack(Items.EXPERIENCE_BOTTLE);
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().scale(2.0F, 2.0F, 2.0F);
        pGuiGraphics.pose().translate(0.5, 0.0, 0.0);
        pGuiGraphics.renderItem(stack, 56, 9);
        pGuiGraphics.pose().popPose();
    }

    protected Pair<Integer, Integer> goButtonPos() {
        return new Pair(46, 32);
    }
    protected Pair<Integer, Integer> goButtonUV() {
        return new Pair(231, 10);
    }
}
