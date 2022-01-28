package org.sosly.witchesandwizards.world.menu;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.sosly.witchesandwizards.gui.MenuSlot;
import org.sosly.witchesandwizards.item.artifact.FlowerCrownItem;
import org.sosly.witchesandwizards.item.filter.FlowerFilter;
import org.sosly.witchesandwizards.world.ContainerRegistry;

public class FlowerCrownMenu extends ContainerItemMenu implements  MenuSlot.ICallbackMenu {
    private final int START_X = 8;
    private final int START_Y = 50;
    private final int CROWN_Y = 18;
    private final int CROWN_1_X = 26;
    private final int CROWN_2_x = 80;
    private final int CROWN_3_x = 134;

    public FlowerCrownMenu(MenuType<?> type, int id, Inventory playerInventory, Level level, ItemStack crown) {
        super(type, id, playerInventory, level, crown);
    }

    @Override
    public boolean canInsert(ItemStack stack, int slotNumber, Slot slotObject) {
        if (stack.isEmpty()) return false;
        return FlowerFilter.anyMatch(stack::is);
    }

    @Override
    public boolean canTake(ItemStack stack, int slotNumber, Slot slotObject) {
        return true;
    }

    private void setupFlowerCrownInventory() {
        addSlot(new MenuSlot.MenuCallback(this, this.contents, 0, CROWN_1_X, CROWN_Y));
        addSlot(new MenuSlot.MenuCallback(this, this.contents, 1, CROWN_2_x, CROWN_Y));
        addSlot(new MenuSlot.MenuCallback(this, this.contents, 2, CROWN_3_x, CROWN_Y));
    }

    private void setupPlayerInventory() {
        // inventory
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {

                int index = (row * 9) + column + 9;
                int slotX = START_X + (column * ContainerRegistry.SLOT_SIZE);
                int slotY = START_Y + (row * ContainerRegistry.SLOT_SIZE);

                addSlot(new Slot(this.playerInventory, index, slotX, slotY));
            }

        }

        // hotbar
        for (int column = 0; column < 9; column++) {
            int slotX = START_X + (column * ContainerRegistry.SLOT_SIZE);
            addSlot(new Slot(this.playerInventory, column, slotX, ContainerRegistry.HOTBAR_Y));
        }
    }



    @Override
    int setupSlots() {
        this.setupFlowerCrownInventory();
        this.setupPlayerInventory();
        return 3;
    }

    @Override
    public boolean stillValid(Player player) {
        return (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof FlowerCrownItem);
    }
}
