/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class TreeStrideMenu extends AbstractContainerMenu {
    public TreeStrideMenu(int id, Inventory playerInv) {
        this(id, playerInv, null);
    }

    public TreeStrideMenu(int id, Inventory playerInv, Player player) {
        super(MenuRegistry.TREE_STRIDE.get(), id);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
}
