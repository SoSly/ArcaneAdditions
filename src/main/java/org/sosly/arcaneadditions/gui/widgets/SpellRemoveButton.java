/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.lwjgl.glfw.GLFW;
import org.sosly.arcaneadditions.utils.RLoc;

public class SpellRemoveButton extends ExtendedButton {
    private static final net.minecraft.resources.ResourceLocation TEXTURE = RLoc.create("textures/gui/familiar.png");

    private static long handCursor = 0;
    private static long normalCursor = 0;

    public SpellRemoveButton(int x, int y, OnPress onPress) {
        super(x, y, 7, 7, Component.literal("X"), onPress);
        if (handCursor == 0) {
            handCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);
            normalCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_CURSOR_NORMAL);
        }
    }

    private boolean wasHovered = false;

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (!this.visible) {
            return;
        }

        boolean currentlyHovered = this.isHovered();
        if (currentlyHovered != wasHovered) {
            long window = Minecraft.getInstance().getWindow().getWindow();
            GLFW.glfwSetCursor(window, currentlyHovered ? handCursor : normalCursor);
            wasHovered = currentlyHovered;
        }

        int u = 199 + 14;
        int v = 0;

        pGuiGraphics.blit(TEXTURE, this.getX(), this.getY(), u, v, this.width, this.height, 256, 256);
    }
}
