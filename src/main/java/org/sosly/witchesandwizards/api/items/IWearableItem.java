package org.sosly.witchesandwizards.api.items;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public interface IWearableItem {
    boolean canEquip(ItemStack stack, EquipmentSlot slot, Entity entity);
}
