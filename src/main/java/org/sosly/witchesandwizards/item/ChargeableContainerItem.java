package org.sosly.witchesandwizards.item;


import com.mna.api.items.ChargeableItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.sosly.witchesandwizards.api.items.IContainerItem;
import org.sosly.witchesandwizards.api.items.WNWCreativeTab;
import org.sosly.witchesandwizards.world.ContainerRegistry;
import org.sosly.witchesandwizards.world.inventory.CurioContainerItemInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * This class provides functionality for chargeable items which are also containers.
 *
 * Chargeable items are a feature of Mana and Artifice, and include items which have mana and use that mana to aid
 * the wearer in some way. Usually these items are curios.
 *
 * Containers are items which can store other items, similar to how a Backpack or Toolbox might function.
 */
public abstract class ChargeableContainerItem extends ChargeableItem implements IContainerItem {
    public ChargeableContainerItem(Properties props, float maxMana) {
        super(props.tab(WNWCreativeTab.TAB_WNW), maxMana);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add((new TranslatableComponent("item.mna.chargeable.mana", this.getMana(stack), this.getMaxMana())).withStyle(ChatFormatting.AQUA));
        tooltip.add((new TranslatableComponent("item.mna.chargeable.pedestalCharge")).withStyle(ChatFormatting.AQUA));

        // todo: change "shift" and "control" to be modifiable keybinds
        //       ideally these should match the keybinds used by Mana and Artifice
        tooltip.add((new TranslatableComponent("item.wnw.chargeable.charge-with", "shift")).withStyle(ChatFormatting.AQUA));
        tooltip.add((new TranslatableComponent("item.mna.item-with-gui.open-with", "control")).withStyle(ChatFormatting.AQUA));
    }

    @Nullable
    public ContainerRegistry.ItemContainerType<?> getContainerType() {
        return null;
    }

    @Nonnull
    public abstract Component getDisplayName();

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(@NotNull ItemStack stack, @Nullable CompoundTag nbt) {
        if (!stack.isEmpty()) {
            return new CurioContainerItemInventory(stack, super.initCapabilities(stack, nbt));
        }

        return null;
    }

    protected void openGui(Level level, Player player, InteractionHand hand) {
        EquipmentSlot slot = (hand == InteractionHand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        ItemStack stack = player.getItemBySlot(slot);

        if (!level.isClientSide()) {
            NetworkHooks.openGui((ServerPlayer) player, new MenuProvider() {
                @Nonnull
                @Override
                public Component getDisplayName() {
                    return ((ChargeableContainerItem)stack.getItem()).getDisplayName();
                }

                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
                    if (!(stack.getItem() instanceof ChargeableContainerItem)) return null;
                    ContainerRegistry.ItemContainerType<?> container = ((ChargeableContainerItem)stack.getItem()).getContainerType();
                    if (container == null) return null;
                    return container.create(id, playerInventory, level, stack);
                }
            }, buffer -> buffer.writeInt(slot.ordinal()));
        }
    }
}
