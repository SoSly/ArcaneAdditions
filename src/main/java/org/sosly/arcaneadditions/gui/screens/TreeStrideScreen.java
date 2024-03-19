/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.gui.screens;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.sosly.arcaneadditions.capabilities.treestride.TreestrideProvider;
import org.sosly.arcaneadditions.gui.menus.TreeStrideMenu;
import org.sosly.arcaneadditions.gui.widgets.TreeStrideDelete;
import org.sosly.arcaneadditions.gui.widgets.TreeStrideDestination;
import org.sosly.arcaneadditions.networking.PacketHandler;
import org.sosly.arcaneadditions.networking.messages.serverbound.NewTreeStrideDestination;
import org.sosly.arcaneadditions.networking.messages.serverbound.RemoveTreeStrideDestination;
import org.sosly.arcaneadditions.networking.messages.serverbound.RequestSyncTreeStrideCapabilitiesFromServer;
import org.sosly.arcaneadditions.networking.messages.serverbound.TreeStridePlayer;
import org.sosly.arcaneadditions.utils.RLoc;

import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TreeStrideScreen extends AbstractContainerScreen<TreeStrideMenu> {
    private Player player;
    private static final ResourceLocation TEXTURE = RLoc.create("textures/gui/tree_stride.png");
    private ExtendedButton createDestinationButton;
    private EditBox createDestinationBox;

    public TreeStrideScreen(TreeStrideMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.player = inventory.player;
        this.leftPos = 0;
        this.topPos = 0;
        this.imageWidth = 176;
        this.imageHeight = 150;
        this.titleLabelX = 8;
        this.titleLabelY = 5;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        Minecraft.getInstance().level.getCapability(TreestrideProvider.TREESTRIDE).ifPresent(treestride -> {
            Map<BlockPos, String> destinations = treestride.getPlayerDestinations(Minecraft.getInstance().player);
            if (destinations != null) {
                AtomicInteger topPos = new AtomicInteger(2);

                destinations.entrySet()
                    .stream()
                    .sorted(Map.Entry.<BlockPos, String>comparingByValue())
                    .forEach(destination -> {
                        topPos.addAndGet(12);
                        addRenderableWidget(new TreeStrideDelete(this.leftPos + 10, this.topPos + topPos.get(), 14, 14, Component.literal("X"), btn -> this.deleteDestination(destination.getKey(), destination.getValue())));
                        addRenderableWidget(new TreeStrideDestination(this.leftPos + 24, this.topPos + topPos.get(), 130, 14, Component.literal(destination.getValue()), btn -> this.teleportPlayer(destination.getKey(), destination.getValue())));
                    });

                if (destinations.size() >= 7) {
                    this.createDestinationBox.visible = false;
                    this.createDestinationButton.visible = false;
                }
            }
        });

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, Color.lightGray.getRGB(), true);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float partialTicks, int mouse_x, int mouse_y) {
        this.renderBackground(pGuiGraphics);
        int xPos = this.leftPos;
        int yPos = this.topPos;
        pGuiGraphics.blit(TEXTURE, xPos, yPos, 0.0F, 0.0F, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    public void init() {
        PacketHandler.network.sendToServer(new RequestSyncTreeStrideCapabilitiesFromServer());
        super.init();

        EditBox newDestination = new EditBox(this.font, this.leftPos + 8,
                this.topPos + 128, 117, 12, this.createDestinationBox,
                Component.translatable("arcaneadditions:components/tree_stride.new_destination"));
        newDestination.setMaxLength(24);
        this.createDestinationBox = addRenderableWidget(newDestination);
        this.createDestinationButton = addRenderableWidget(new ExtendedButton(this.leftPos + 128,
                this.topPos + 126, 40, 16, Component.translatable("arcaneadditions:components/tree_stride.save"),
                btn -> this.handleNewDestination()));
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        InputConstants.Key mouseKey = InputConstants.getKey(pKeyCode, pScanCode);

        // handle escape
        if (mouseKey.getValue() == 256) {
            this.onClose();
            return true;
        }

        if (this.createDestinationBox.canConsumeInput()) {
            // handle enter key
            if (mouseKey.getValue() == 257) {
                return this.handleNewDestination();
            }
            return this.createDestinationBox.keyPressed(pKeyCode, pScanCode, pModifiers);
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    private void deleteDestination(BlockPos pos, String name) {
        PacketHandler.network.sendToServer(new RemoveTreeStrideDestination(pos));
        this.onClose();
        Minecraft.getInstance().player.sendSystemMessage(Component.translatable("arcaneadditions:components/tree_stride.removed_destination", name));
    }

    private boolean handleNewDestination() {
        String newValue = this.createDestinationBox.getValue();
        if (newValue.length() == 0) {
            return false;
        }

        PacketHandler.network.sendToServer(new NewTreeStrideDestination(Component.literal(newValue)));

        this.onClose();
        Minecraft.getInstance().player.sendSystemMessage(Component.translatable("arcaneadditions:components/tree_stride.added_destination", newValue));
        return true;
    }

    private void teleportPlayer(BlockPos pos, String name) {
        this.onClose();
        PacketHandler.network.sendToServer(new TreeStridePlayer(pos));
        Minecraft.getInstance().player.sendSystemMessage(Component.translatable("arcaneadditions:components/tree_stride.travel", name));
    }
}
