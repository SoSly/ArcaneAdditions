package org.sosly.arcaneadditions.gui.menus;

import com.mna.gui.containers.block.SimpleWizardLabContainer;
import com.mna.gui.containers.slots.ItemFilterSlot;
import com.mna.gui.containers.slots.SingleItemSlot;
import com.mna.items.ItemInit;
import com.mna.items.filters.SpellItemFilter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.sosly.arcaneadditions.blocks.tileentities.ScribesBenchTile;
import org.sosly.arcaneadditions.gui.MenuRegistry;

public class ScribesBenchMenu extends SimpleWizardLabContainer<ScribesBenchMenu, ScribesBenchTile> {
    public ScribesBenchMenu(int i, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
        super(MenuRegistry.SCRIBES_BENCH.get(), i, playerInventory, packetBuffer);
    }

    public ScribesBenchMenu(int id, Inventory playerInventory, ScribesBenchTile tile) {
        super(MenuRegistry.SCRIBES_BENCH.get(), id, playerInventory, tile);
    }

    @Override
    protected int addInventorySlots() {
        this.addSlot(new SingleItemSlot(this.tileItemHandler, 0, 24, 8, ItemInit.ARCANIST_INK.get()));
        this.addSlot(new SingleItemSlot(this.tileItemHandler, 1, 47, 8, Items.LAPIS_LAZULI));
        this.addSlot(new SingleItemSlot(this.tileItemHandler, 2, 24, 31, ItemInit.SPELL.get()));
        this.addSlot((new ItemFilterSlot(this.tileItemHandler, 3, 80, 31, new SpellItemFilter())).setMaxStackSize(1));
        return 4;
    }

    public int getInkRequired() {
        return this.tile.getInkRequired();
    }

    public int getLapisRequired(Player player) {
        return this.tile.getLapisRequired(player);
    }

    protected int playerInventoryXStart() {
        return 8;
    }

    protected int playerInventoryYStart() {
        return 66;
    }
}
