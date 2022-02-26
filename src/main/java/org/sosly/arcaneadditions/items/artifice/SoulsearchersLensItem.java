/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.items.artifice;

import com.mna.api.ManaAndArtificeMod;
import com.mna.api.sound.SFX;
import com.mna.entities.constructs.animated.EntityAnimatedConstruct;
import com.mna.items.sorcery.ItemCrystalPhylactery;
import com.mna.items.sorcery.PhylacteryStaffItem;
import com.mna.sound.ItemInUseLoopingSound;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sosly.arcaneadditions.api.AdditionalTieredItem;
import org.sosly.arcaneadditions.config.ServerConfig;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SoulsearchersLensItem extends AdditionalTieredItem {
    private static final Lazy<ItemCrystalPhylactery> CrystalPhylactery = Lazy.of(() -> (ItemCrystalPhylactery)ForgeRegistries.ITEMS.getValue(new ResourceLocation("mna:crystal_phylactery")));
    private static final Lazy<PhylacteryStaffItem> StaffPhylactery = Lazy.of(() -> (PhylacteryStaffItem)ForgeRegistries.ITEMS.getValue(new ResourceLocation("mna:staff_phylactery")));


    public SoulsearchersLensItem() {
        super(new Properties());
    }

    @Override
    public UseAnim getUseAnimation(@NotNull ItemStack item) {
        return UseAnim.SPYGLASS;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack item) {
        return 999999;
    }

    // Right Click - if targeting a creature
    //      ensure the user has a phylactery
    //      target health multiplier based on creature type (config, e.g.: "minecraft:villager,2.5" = 2.5 * health before XP calc)
    //      drain XP from the user based on target health (if they have enough, otherwise cancel action)
    //      phylactery gains +1 as if the user killed the creature
    //      item goes on cooldown
    //      possibly add capability to the target; you can't target that specific creature again

    @Override
    public InteractionResult interactLivingEntity(@NotNull ItemStack item, Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        if (target instanceof Mob && !(target instanceof EntityAnimatedConstruct)) {
            item.getOrCreateTag().putInt("target", target.getId());
            item.getOrCreateTag().putString("hand", hand.toString());
            return InteractionResult.PASS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack lens, int ticks) {
        if (ticks % 20 == 0 && user instanceof Player) {
            Player player = (Player)user;

            MutableBoolean continueUsing = new MutableBoolean(true);
            player.getCapability(ManaAndArtificeMod.getMagicCapability()).ifPresent((m) -> {
                if (m.isMagicUnlocked()) {
                    Mob target = this.getTargetFromTag(lens, level);
                    InteractionHand hand = this.getHandFromTag(lens);
                    ItemStack phylactery = hand == InteractionHand.MAIN_HAND ? user.getOffhandItem() : user.getMainHandItem();

                    if (target != null && phylactery.getCount() > 0) {
                        if (!this.useOn(level, player, target, phylactery)) {
                            continueUsing.setFalse();
                        }
                    } else {
                        continueUsing.setFalse();
                    }
                } else {
                    continueUsing.setFalse();
                }
            });

            if (!continueUsing.getValue()) {
                player.releaseUsingItem();
            }
        }
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        final ItemStack lens = (hand == InteractionHand.MAIN_HAND) ? player.getMainHandItem() : player.getOffhandItem();
        final ItemStack phylactery = (hand == InteractionHand.MAIN_HAND) ? player.getOffhandItem() : player.getMainHandItem();
        AtomicBoolean result = new AtomicBoolean(false);

        Mob target = this.getTargetFromTag(lens, level);
        if (target != null ) {
            player.getCapability(ManaAndArtificeMod.getMagicCapability()).ifPresent((m) -> {
                if (m.isMagicUnlocked()) {
                    if (this.isPhylacteryItem(phylactery)) {
                        if ((PhylacteryStaffItem.getEntityType(phylactery) == target.getType() && !PhylacteryStaffItem.isFilled(phylactery))
                                || PhylacteryStaffItem.getEntityType(phylactery) == null) {
                            if (level.isClientSide) {
                                this.PlayLoopingSound(SFX.Loops.ARCANE, player);
                            }
                            player.startUsingItem(hand);
                            result.set(true);
                        } else {
                            if (level.isClientSide) {
                                player.sendMessage(new TranslatableComponent("item.arcaneadditions.soulsearchers_lens.nonphylactery"), UUID.randomUUID());
                            }
                        }
                    }
                } else {
                    if (level.isClientSide) {
                        player.sendMessage(new TranslatableComponent("item.arcaneadditions.soulsearchers_lens.confusion"), UUID.randomUUID());
                    }
                }
            });
        }

        return result.get() ? InteractionResultHolder.pass(lens) : InteractionResultHolder.fail(lens);
    }

    @Override
    public boolean canContinueUsing(@NotNull ItemStack oldStack, ItemStack newStack) {
        return true;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity user, int ticks) {
        if (stack.hasTag()) {
            assert stack.getTag() != null;
            stack.getTag().remove("target");
            stack.getTag().remove("phylactery");

            if (user instanceof Player) {
                ((Player)user).getCooldowns().addCooldown(this, 100);
            }
        }
    }

    private boolean useOn(@NotNull Level level, @NotNull Player player, @NotNull Mob target, ItemStack phylactery) {
        float adjustedHealth = target.getMaxHealth() * this.getAdjustmentForType(target.getType());
        int levelsRequired = (int)Math.floor(adjustedHealth / ServerConfig.SOULSEARCHERS_LENS_HEALTH_PER_LEVEL.get());

        if (target.distanceTo(player) > (float)ServerConfig.SOULSEARCHERS_LENS_MAX_DISTANCE.get()) {
            if (level.isClientSide) {
                player.sendMessage(new TranslatableComponent("item.arcaneadditions.soulsearchers_lens.distance"), UUID.randomUUID());
            }
            return false;
        }

        if (player.experienceLevel < levelsRequired) {
            if (level.isClientSide) {
                player.sendMessage(new TranslatableComponent("item.arcaneadditions.soulsearchers_lens.experience"), UUID.randomUUID());
            }
            return false;
        }

        @SuppressWarnings(value="unchecked")
        EntityType<? extends Mob> type = (EntityType<? extends Mob>)target.getType();
        float amount = player.isCreative() ? 100 : 1;
        boolean added = this.addToPhylactery(player, phylactery, type, amount, target.level);

        if (added) {
            player.giveExperienceLevels(-1 * levelsRequired);
        }

        return added;
    }

    @Nullable
    private Mob getTargetFromTag(ItemStack stack, Level level) {
        if (stack.hasTag()) {
            assert stack.getTag() != null;
            return (Mob)level.getEntity(stack.getTag().getInt("target"));
        }

        return null;
    }

    @Nullable
    private InteractionHand getHandFromTag(ItemStack stack) {
        if (stack.hasTag()) {
            assert stack.getTag() != null;
            return InteractionHand.valueOf(stack.getTag().getString("hand"));
        }

        return null;
    }

    @Nullable
    private float getAdjustmentForType(EntityType entityType) {
        String type = entityType.getRegistryName().toString();
        AtomicReference<Float> modifier = new AtomicReference<>(1.0f);
        ServerConfig.SOULSEARCHERS_LENS_CREATURE_MODIFIERS.get().stream().forEach((mod) -> {
            String[] parts = mod.split(",");
            String registryName = parts[0];
            String stringModifier = parts[1];
            if (type.equals(registryName)) {
                 modifier.set(Float.parseFloat(stringModifier));
            }
        });
        return modifier.get();
    }

    private boolean addToPhylactery(Player player, @NotNull ItemStack phylactery, EntityType<? extends Mob> type, float amount, Level level) {
        if (phylactery.getItem() == CrystalPhylactery.get()) {
            return ItemCrystalPhylactery.addToPhylactery(player.getInventory(), type, amount, level, true);
        } else {
            return PhylacteryStaffItem.addToPhylactery(phylactery, type, amount, level);
        }
    }

    private boolean isPhylacteryItem(@NotNull ItemStack phylactery) {
        return phylactery.getItem() == CrystalPhylactery.get() || phylactery.getItem() == StaffPhylactery.get();
    }

    @OnlyIn(Dist.CLIENT)
    private void PlayLoopingSound(SoundEvent soundID, Player player) {
        Minecraft.getInstance().getSoundManager().play(new ItemInUseLoopingSound(soundID, player));
    }
}
