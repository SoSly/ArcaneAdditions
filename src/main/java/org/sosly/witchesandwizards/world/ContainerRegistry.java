package org.sosly.witchesandwizards.world;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.sosly.witchesandwizards.WitchesAndWizards;
import org.sosly.witchesandwizards.world.menu.FlowerCrownMenu;

import javax.annotation.Nullable;

public class ContainerRegistry {
    public static final int SLOT_SIZE = 18;
    public static final int HOTBAR_Y = 108;
    public static final int PLAYER_INVENTORY_SLOTS = 9 * 4;

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, WitchesAndWizards.MOD_ID);

    public static final ItemContainerType<FlowerCrownMenu> FLOWER_CROWN = register("flower_crown", FlowerCrownMenu::new);

    public static <C extends AbstractContainerMenu> ItemContainerType<C> register(String name, ItemContainerConstructor<C> container) {
        RegistryObject<MenuType<C>> reference = CONTAINERS.register(name, () -> {
            Mutable<MenuType<C>> menu = new MutableObject<>();
            MenuType<C> type = new MenuType<>((IContainerFactory<C>)(windowId, inv, data) -> {
                Level level = Minecraft.getInstance().level;
                int ordinal = data.readInt();
                EquipmentSlot slot = EquipmentSlot.values()[ordinal];
                Player player = Minecraft.getInstance().player;
                ItemStack stack = null;
                if (player != null) {
                    stack = player.getItemBySlot(slot);
                }
                return container.construct(menu.getValue(), windowId, inv, level, stack);
            });
            menu.setValue(type);
            return type;
        });
        return new ItemContainerType<>(reference, container);
    }

    public interface ItemContainerConstructor<M extends AbstractContainerMenu> {
        M construct(MenuType<M> type, int windowId, Inventory inventoryPlayer, Level world, @Nullable ItemStack stack);
    }

    public static class ItemContainerType<M extends AbstractContainerMenu> {
        final RegistryObject<MenuType<M>> type;
        final ItemContainerConstructor<M> factory;

        private ItemContainerType(RegistryObject<MenuType<M>> type, ItemContainerConstructor<M> factory) {
            this.type = type;
            this.factory = factory;
        }

        public M create(int id, Inventory inv, Level w, ItemStack stack) {
            return factory.construct(getType(), id, inv, w, stack);
        }

        public MenuType<M> getType() {
            return type.get();
        }
    }
}
