package org.sosly.witchesandwizards.item.artifact;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.sosly.witchesandwizards.api.items.IWearableItem;
import org.sosly.witchesandwizards.item.ChargeableContainerItem;
import org.sosly.witchesandwizards.item.filter.FlowerFilter;
import org.sosly.witchesandwizards.world.ContainerRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * A decorative Flower Crown with customizable flowers
 *
 * The flower crown can hold up to 3 flowers of the owner's choice. The flowers chosen are reflected in crown's visual
 * appearance.  If the wearer supplies the crown with mana and puts in magical flowers, they may receive special buffs
 * while the mana remains.
 */
public class FlowerCrownItem extends ChargeableContainerItem implements IWearableItem {
    private static final int DURATION = 60;
    private static final float MAX_MANA = 2000.0f;
    private static final int SLOT_COUNT = 3;
    public static final String NBT_ID = "mna:flower_crown_data";
    private static final Component TITLE = new TranslatableComponent("item.wnw.flower_crown");

    private int applied = 0;

    public FlowerCrownItem() {
        super(new Properties().stacksTo(1), MAX_MANA);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot slot, Entity entity) {
        return (entity instanceof Player && stack.getItem() == this && slot == EquipmentSlot.HEAD);
    }

    @Override
    public boolean consumeMana(ItemStack stack, float amount, @Nullable Player player) {
        boolean consumed = false;
        while (applied > 0) {
            consumed = super.consumeMana(stack, amount, player);
            --applied;
        }
        return consumed;
    }

    @Nullable
    public ContainerRegistry.ItemContainerType<?> getContainerType() {
        return ContainerRegistry.FLOWER_CROWN;
    }

    @Nonnull
    @Override
    public Component getDisplayName() {
        return TITLE;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.AZALEA_LEAVES_FALL;
    }

    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }


    @Override
    public int getSlotCount() {
        return SLOT_COUNT;
    }

    @Override
    protected float manaPerRechargeTick() {
        return 5.0F;
    }

    @Override
    public void onArmorTick(ItemStack stack, Level worldIn, Player player) {
        float mana = this.getMana(stack);
        int slot = stack.getEquipmentSlot().getIndex();
        if (mana >= this.manaPerOperation() && this.tickEffect(stack, player, player.level, slot, mana, false)) {
            this.consumeMana(stack, this.manaPerOperation(), player);
        }
    }

    @Override
    protected boolean tickEffect(ItemStack itemStack, Player player, Level level, int slot, float mana, boolean selected) {
        IItemHandler inventory = itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(RuntimeException::new);
        for (int idx = 0; idx < inventory.getSlots(); idx++) {
            final ItemStack flower = inventory.getStackInSlot(idx);
            if (flower.is(FlowerFilter.MAGIC_FLOWERS)) {
                MobEffect effect = FlowerFilter.getFlowerEffect(flower.getItem());
                if (effect != null) {
                    MobEffectInstance inst = new MobEffectInstance(effect, DURATION);
                    if (!player.hasEffect(effect)) {
                        player.addEffect(inst);
                        applied++;
                    }
                }
            }
        }

        return applied != 0;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // todo: change "shift" and "control" to be modifiable keybinds
        //       ideally these should match the keybinds used by Mana and Artifice
        if (Screen.hasShiftDown()) return this.useRecharge(level, player, hand);
        else if (Screen.hasControlDown()) return this.useInventory(level, player, hand);
        else return this.useEquip(level, player, hand);
    }

    public InteractionResultHolder<ItemStack> useEquip(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(stack);
        ItemStack wearing = player.getItemBySlot(slot);
        if (wearing.isEmpty()) {
            player.setItemSlot(slot, stack.copy());
            stack.setCount(0);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
        return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
    }

    public InteractionResultHolder<ItemStack> useInventory(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        openGui(level, player, hand);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    public InteractionResultHolder<ItemStack> useRecharge(Level level, Player player, InteractionHand hand) {
        return super.use(level, player, hand);
    }
}
