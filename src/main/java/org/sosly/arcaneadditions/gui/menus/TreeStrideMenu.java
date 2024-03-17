/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.gui.menus;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.sosly.arcaneadditions.capabilities.treestride.ITreestrideCapability;
import org.sosly.arcaneadditions.capabilities.treestride.TreestrideProvider;

public class TreeStrideMenu extends AbstractContainerMenu {
    ITreestrideCapability capability;

    public TreeStrideMenu(int id, Inventory playerInv) {
        this(id, playerInv, playerInv.player);

    }

    public TreeStrideMenu(int id, Inventory playerInv, Player player) {
        super(MenuRegistry.TREE_STRIDE.get(), id);
        player.level().getCapability(TreestrideProvider.TREESTRIDE).ifPresent(cap -> {
            capability = cap;
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int pIndex) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        if (player.level().isClientSide()) {
            return true;
        }

        if (capability == null) {
            return false;
        }

        BlockPos playerPos = player.getOnPos();
        BlockPos rootPos = capability.getCurrentPosition((ServerPlayer) player);

        return rootPos != null && playerPos.distManhattan(rootPos) < player.getBlockReach();
    }
}
