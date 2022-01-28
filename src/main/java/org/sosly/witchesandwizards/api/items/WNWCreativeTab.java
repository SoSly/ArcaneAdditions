package org.sosly.witchesandwizards.api.items;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.sosly.witchesandwizards.WitchesAndWizards;
import org.sosly.witchesandwizards.item.ItemRegistry;

public class WNWCreativeTab extends CreativeModeTab {
    public static final WNWCreativeTab TAB_WNW = new WNWCreativeTab();
    private ItemStack icon;

    WNWCreativeTab() {
        super(WitchesAndWizards.MOD_ID);
    }

    @Override
    @NotNull
    public ItemStack makeIcon() {
        if (icon == null) {
            icon = new ItemStack(ItemRegistry.FLOWER_CROWN.get()); // todo
        }
        return icon;
    }
}
