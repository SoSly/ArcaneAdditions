/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.api;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.items.ItemRegistry;

public class AACreativeTab extends CreativeModeTab {
    public static final AACreativeTab TAB_AA = new AACreativeTab();
    private ItemStack icon;

    AACreativeTab() {
        super(ArcaneAdditions.MOD_ID);
    }

    @Override
    @NotNull
    public ItemStack makeIcon() {
        if (icon == null) {
            icon = new ItemStack(ItemRegistry.SOULSEARCHERS_LENS.get());
        }
        return icon;
    }
}
