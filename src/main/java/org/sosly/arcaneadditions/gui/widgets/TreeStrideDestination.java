package org.sosly.arcaneadditions.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.awt.*;

public class TreeStrideDestination extends ExtendedButton {
    public TreeStrideDestination(int xPos, int yPos, int width, int height, Component displayString, OnPress handler) {
        super(xPos, yPos, width, height, displayString, handler);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        Color color = this.isHoveredOrFocused() ? Color.lightGray : Color.gray;
        guiGraphics.drawString(mc.font, Language.getInstance().getVisualOrder(this.getMessage()), this.getX() , this.getY() + (this.height - 8) / 2, color.getRGB());
    }
}
