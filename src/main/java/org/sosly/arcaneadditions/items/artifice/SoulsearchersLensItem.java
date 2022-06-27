/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.items.artifice;

import com.mna.api.ManaAndArtificeMod;
import com.mna.api.entities.construct.IConstruct;
import com.mna.api.items.IPhylacteryItem;
import com.mna.api.sound.SFX;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sosly.arcaneadditions.configs.ServerConfig;
import org.sosly.arcaneadditions.entities.EntityRegistry;
import org.sosly.arcaneadditions.entities.sorcery.SoulSearchersBeamEntity;
import org.sosly.arcaneadditions.sounds.UseItemTickingSoundInstance;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SoulsearchersLensItem extends Item {
    private static final String TARGET_KEY = "soulsearcher-target";
    public SoulsearchersLensItem() {
        super(new Properties());
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack item) {
        return UseAnim.SPYGLASS;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack item) {
        return 999999;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack item, Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        if (target instanceof Mob && !(target instanceof IConstruct)) {
            player.getPersistentData().putInt(TARGET_KEY, target.getId());
            return InteractionResult.PASS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack lens, int ticks) {
        if (ticks % 20 == 0 && user instanceof Player player) {
            int beamID = lens.getOrCreateTag().getInt("beam");
            SoulSearchersBeamEntity beam = (SoulSearchersBeamEntity) level.getEntity(beamID);
            if (beam != null) {
                beam.setPos(user.getEyePosition());
            }

            MutableBoolean continueUsing = new MutableBoolean(true);
            player.getCapability(ManaAndArtificeMod.getMagicCapability()).ifPresent((m) -> {
                if (m.isMagicUnlocked()) {
                    int targetId = player.getPersistentData().getInt(TARGET_KEY);
                    Mob target = (Mob) level.getEntity(targetId);
                    InteractionHand hand = player.getUsedItemHand();
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
        final ItemStack itemStack = (hand == InteractionHand.MAIN_HAND) ? player.getOffhandItem() : player.getMainHandItem();
        AtomicBoolean result = new AtomicBoolean(false);

            int targetID = player.getPersistentData().getInt(TARGET_KEY);
            Mob target = (Mob) level.getEntity(targetID);
            if (target != null) {
                player.getCapability(ManaAndArtificeMod.getMagicCapability()).ifPresent((m) -> {
                    if (m.isMagicUnlocked()) {
                        Item item = itemStack.getItem();
                        if (item instanceof IPhylacteryItem phylactery) {
                            if (!phylactery.isFull(itemStack)) {
                                if (level.isClientSide()) {
                                    this.PlayLoopingSound(SFX.Loops.ARCANE, player);
                                } else {
                                    SoulSearchersBeamEntity beam = new SoulSearchersBeamEntity(EntityRegistry.SOUL_SEARCHERS_BEAM.get(), player.getLevel());
                                    beam.setSource(player);
                                    beam.setTarget(target);
                                    beam.setPos(player.getEyePosition());
                                    lens.getOrCreateTag().putInt("beam", beam.getId());
                                    level.addFreshEntity(beam);
                                }
                                player.startUsingItem(hand);
                                result.set(true);
                            } else {
                                if (level.isClientSide()) {
                                    player.sendMessage(new TranslatableComponent("item.arcaneadditions.soulsearchers_lens.nonphylactery"), UUID.randomUUID());
                                }
                            }
                        }
                    } else {
                        if (level.isClientSide()) {
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
        if (user instanceof Player) {
            user.getPersistentData().remove(TARGET_KEY);
            ((Player)user).getCooldowns().addCooldown(this, 40);
            final ItemStack lens = (user.getUsedItemHand() == InteractionHand.MAIN_HAND) ? user.getMainHandItem() : user.getOffhandItem();
            int beamID = lens.getOrCreateTag().getInt("beam");
            SoulSearchersBeamEntity beam = (SoulSearchersBeamEntity)level.getEntity(beamID);
            if (beam != null) {
                beam.remove(Entity.RemovalReason.DISCARDED);
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

        if (player.experienceLevel < levelsRequired && !player.isCreative()) {
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
        float current = ((IPhylacteryItem)phylactery.getItem()).getContainedSouls(phylactery);
        return ((IPhylacteryItem)phylactery.getItem()).fill(phylactery, type, current + amount, level);
    }

    @OnlyIn(Dist.CLIENT)
    private void PlayLoopingSound(SoundEvent soundID, Player player) {
        Minecraft.getInstance().getSoundManager().play(new UseItemTickingSoundInstance(soundID, player));
    }
}
