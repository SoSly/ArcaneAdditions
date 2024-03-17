package org.sosly.arcaneadditions.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.awt.*;

public class TreeStrideDelete extends ExtendedButton {
    public TreeStrideDelete(int xPos, int yPos, int width, int height, Component displayString, OnPress handler) {
        super(xPos, yPos, width, height, displayString, handler);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        final FormattedText buttonText = mc.font.ellipsize(this.getMessage(), this.width - 6); // Remove 6 pixels so that the text is always contained within the button's borders
        Color color = this.isHoveredOrFocused() ? Color.red : Color.gray;
        guiGraphics.drawString(mc.font, Language.getInstance().getVisualOrder(buttonText), this.getX() , this.getY() + (this.height - 8) / 2, color.getRGB());
    }
}
