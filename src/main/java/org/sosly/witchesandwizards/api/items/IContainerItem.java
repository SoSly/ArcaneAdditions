package org.sosly.witchesandwizards.api.items;

import net.minecraftforge.items.IItemHandler;
import org.sosly.witchesandwizards.world.ContainerRegistry;

public interface IContainerItem {
    ContainerRegistry.ItemContainerType<?> getContainerType();
    int getSlotCount();
}
