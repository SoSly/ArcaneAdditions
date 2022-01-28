package org.sosly.witchesandwizards.world.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.sosly.witchesandwizards.world.ContainerRegistry;
import org.sosly.witchesandwizards.world.inventory.CurioContainerItemInventory;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public abstract class ContainerItemMenu extends AbstractContainerMenu implements Supplier<Level> {
    protected final IItemHandler contents;
    protected final ItemStack held;
    protected final Level level;
    protected final Player player;
    protected final Inventory playerInventory;
    protected final int size;

    public ContainerItemMenu(MenuType<?> type, int id, Inventory playerInventory, Level level, ItemStack held) {
        super(type, id);

        this.held = held;
        this.level = level;
        this.player = playerInventory.player;
        this.playerInventory = playerInventory;

        this.contents = held.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(RuntimeException::new);

        if (this.contents instanceof CurioContainerItemInventory) {
            ((CurioContainerItemInventory)contents).setInventoryForUpdate(playerInventory);
        }

        this.size = this.setupSlots();
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(Player playerIn, int slot) {
        Slot source = slots.get(slot);
        if (!source.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = source.getItem();
        ItemStack copy = stack.copy();

        // Check if the clicked slot is in a container slot
        if (slot < this.size) {
            if (!moveItemStackTo(stack, this.size, ContainerRegistry.PLAYER_INVENTORY_SLOTS, false)) {
                return ItemStack.EMPTY;
            }
        } else if (slot < this.size + ContainerRegistry.PLAYER_INVENTORY_SLOTS) {
            if (!moveItemStackTo(stack, 0, this.size, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slot index:" + slot);
            return ItemStack.EMPTY;
        }

        // If stack size is 0 then the entire stack was moved. Set the slot contents to null.
        if (stack.getCount() == 0) {
            source.set(ItemStack.EMPTY);
        } else {
            source.setChanged();
        }

        source.onTake(playerIn, stack);
        return copy;
    }

    abstract int setupSlots();

    @Override
    public Level get() {
        return level;
    }
}
