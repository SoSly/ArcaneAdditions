package org.sosly.witchesandwizards.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.sosly.witchesandwizards.WitchesAndWizards;
import org.sosly.witchesandwizards.item.artifact.FlowerCrownItem;

/**
 * ItemRegistry creates a deferred register for all  the items in this mod.
 *
 * All items must be registered with the ITEMS registry.
 */
public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WitchesAndWizards.MOD_ID);

    public static final RegistryObject<FlowerCrownItem> FLOWER_CROWN = ITEMS.register("flower_crown", FlowerCrownItem::new);
}
