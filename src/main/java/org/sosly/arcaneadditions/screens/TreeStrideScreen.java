/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.sosly.arcaneadditions.capabilities.treestride.TreestrideProvider;
import org.sosly.arcaneadditions.menus.TreeStrideMenu;
import org.sosly.arcaneadditions.networking.PacketHandler;
import org.sosly.arcaneadditions.networking.messages.serverbound.NewTreeStrideDestination;
import org.sosly.arcaneadditions.networking.messages.serverbound.RemoveTreeStrideDestination;
import org.sosly.arcaneadditions.networking.messages.serverbound.RequestSyncTreeStrideCapabilitiesFromServer;
import org.sosly.arcaneadditions.networking.messages.serverbound.TreeStridePlayer;
import org.sosly.arcaneadditions.utils.RLoc;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TreeStrideScreen extends AbstractContainerScreen<TreeStrideMenu> {
    private static final ResourceLocation TEXTURE = RLoc.create("textures/gui/tree_stride.png");
    private ExtendedButton createDestinationButton;
    private EditBox createDestinationBox;

    public TreeStrideScreen(TreeStrideMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.leftPos = 0;
        this.topPos = 0;
        this.imageWidth = 176;
        this.imageHeight = 150;
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        Minecraft.getInstance().level.getCapability(TreestrideProvider.TREESTRIDE).ifPresent(treestride -> {
            Map<BlockPos, String> destinations = treestride.getPlayerDestinations(Minecraft.getInstance().player);
            if (destinations != null) {
                AtomicInteger topPos = new AtomicInteger();

                destinations.entrySet()
                    .stream()
                    .sorted(Map.Entry.<BlockPos, String>comparingByValue())
                    .forEach(destination -> {
                        topPos.addAndGet(18);
                        addRenderableWidget(new ExtendedButton(this.leftPos + 7, this.topPos + topPos.get(), 140, 16, Component.literal(destination.getValue()), btn -> this.teleportPlayer(destination.getKey(), destination.getValue())));
                        addRenderableWidget(new ExtendedButton(this.leftPos + 152, this.topPos + topPos.get(), 16, 16, Component.literal("X"), btn -> this.deleteDestination(destination.getKey(), destination.getValue())));
                    });

                if (destinations.size() >= 7) {
                    this.createDestinationBox.visible = false;
                    this.createDestinationButton.visible = false;
                }
            }
        });

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.font.draw(pPoseStack, this.title, this.leftPos + 20, this.topPos + 5, 0x404040);
    }

    @Override
    public void init() {
        PacketHandler.network.sendToServer(new RequestSyncTreeStrideCapabilitiesFromServer());
        super.init();

        this.createDestinationBox = addRenderableWidget(new EditBox(this.font, this.leftPos + 15,
                this.topPos + 126, 100, 15, this.createDestinationBox,
                Component.translatable("arcaneadditions:components/tree_stride.new_destination")));
        this.createDestinationButton = addRenderableWidget(new ExtendedButton(this.leftPos + 120,
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

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        renderBackground(pPoseStack);
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(pPoseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {}
}
