package org.sosly.witchesandwizards.world.inventory;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.sosly.witchesandwizards.api.items.IContainerItem;
import top.theillusivec4.curios.api.CuriosCapability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Provides capabilities for curios and item storage.
 */
public class CurioContainerItemInventory extends ItemStackHandler implements ICapabilityProvider {
    public static final int SLOT_SIZE = 1;

    private final LazyOptional<IItemHandler> storage = createStorageCapability(this);
    private ICapabilityProvider curioProvider;

    public CurioContainerItemInventory(ItemStack stack, ICapabilityProvider curioProvider) {
        super();

        this.curioProvider = curioProvider;

        int size = ((IContainerItem)stack.getItem()).getSlotCount();
        NonNullList<ItemStack> list = NonNullList.withSize(size, ItemStack.EMPTY);

        for (int i = 0; i < Math.min(stacks.size(), size); i++) {
            list.set(i, stacks.get(i));
        }

        stacks = list;
    }

    // @credit This section of code was implemented based on ImmserveEngineering's CapabilityUtils.constantOptional.
    //         According to BluSunrize, there is currently a bug in the LazyOptional resolve code that can cause
    //         problems in multithreaded contexts where "resolved" is set to a reference to null during the resolution
    //         on one thread, and any other thread trying to access the value will get null.  Resolving the value
    //         here seems to fix that.
    private static <T> LazyOptional<T> createStorageCapability(T val) {
        LazyOptional<T> result = LazyOptional.of(() -> Objects.requireNonNull(val));
        result.resolve();
        return result;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return storage.cast();
        if (capability == CuriosCapability.ITEM) return curioProvider.getCapability(capability, facing);
        return LazyOptional.empty();
    }

    @Override
    public int getSlotLimit(int slot) { return SLOT_SIZE; }

    public void setInventoryForUpdate(Container inventory) {
        onChange = (inventory != null) ? inventory::setChanged : () -> {};
    }

    @Nonnull
    private Runnable onChange = () -> {};

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        onChange.run();
    }
}
