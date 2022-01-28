package org.sosly.witchesandwizards.gui;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MenuSlot extends Slot {
    final AbstractContainerMenu menu;

    public MenuSlot(AbstractContainerMenu menu, Container inventory, int id, int x, int y) {
        super(inventory, id, x, y);
        this.menu = menu;
    }

    public static class MenuCallback extends SlotItemHandler {
        AbstractContainerMenu menu;

        public MenuCallback(AbstractContainerMenu menu, IItemHandler handler, int id, int x, int y) {
            super(handler, id, x, y);
            this.menu = menu;
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            if (this.menu instanceof ICallbackMenu) {
                return ((ICallbackMenu)this.menu).canInsert(itemStack, getSlotIndex(), this);
            }
            return true;
        }

        @Override
        public boolean mayPickup(Player player) {
            if (this.menu instanceof ICallbackMenu) {
                return ((ICallbackMenu)this.menu).canTake(this.getItem(), getSlotIndex(), this);
            }
            return true;
        }
    }

    public interface ICallbackMenu {
        boolean canInsert(ItemStack stack, int slotNumber, Slot slotObject);
        boolean canTake(ItemStack stack, int slotNumber, Slot slotObject);
    }
}
