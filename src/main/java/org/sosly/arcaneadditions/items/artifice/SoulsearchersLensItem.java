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
import com.mna.api.items.TieredItem;
import com.mna.api.sound.SFX;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.sosly.arcaneadditions.config.ServerConfig;
import org.sosly.arcaneadditions.entities.EntityRegistry;
import org.sosly.arcaneadditions.entities.sorcery.SoulSearchersBeamEntity;
import org.sosly.arcaneadditions.sounds.UseItemTickingSoundInstance;

public class SoulsearchersLensItem extends TieredItem {
    private static final String TARGET_KEY = "soulsearcher-target";
    public SoulsearchersLensItem() {
        super(new Properties().stacksTo(1));
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
        if (!(target instanceof Mob) || target instanceof IConstruct) {
            return InteractionResult.FAIL;
        }

        player.getPersistentData().putInt(TARGET_KEY, target.getId());
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack lens, int ticks) {
        if (ticks % 20 != 0 || !(user instanceof Player player)) {
            return;
        }

        int beamID = lens.getOrCreateTag().getInt("beam");
        SoulSearchersBeamEntity beam = (SoulSearchersBeamEntity) level.getEntity(beamID);
        if (beam != null) {
            beam.setPos(user.getEyePosition());
        }

        var magicCapability = player.getCapability(ManaAndArtificeMod.getMagicCapability()).resolve();
        if (magicCapability.isEmpty()) {
            player.releaseUsingItem();
            return;
        }

        var magic = magicCapability.get();
        if (!magic.isMagicUnlocked()) {
            player.releaseUsingItem();
            return;
        }

        int targetId = player.getPersistentData().getInt(TARGET_KEY);
        Mob target = (Mob) level.getEntity(targetId);
        InteractionHand hand = player.getUsedItemHand();
        ItemStack phylactery = hand == InteractionHand.MAIN_HAND ? user.getOffhandItem() : user.getMainHandItem();

        if (target == null || phylactery.getCount() == 0) {
            player.releaseUsingItem();
            return;
        }

        if (!this.useOn(level, player, target, phylactery)) {
            player.releaseUsingItem();
        }
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        final ItemStack lens = (hand == InteractionHand.MAIN_HAND) ? player.getMainHandItem() : player.getOffhandItem();
        final ItemStack phylacteryStack = (hand == InteractionHand.MAIN_HAND) ? player.getOffhandItem() : player.getMainHandItem();

        int targetID = player.getPersistentData().getInt(TARGET_KEY);
        Mob target = (Mob) level.getEntity(targetID);
        if (target == null) {
            return InteractionResultHolder.fail(lens);
        }

        var magicCapability = player.getCapability(ManaAndArtificeMod.getMagicCapability()).resolve();
        if (magicCapability.isEmpty()) {
            return InteractionResultHolder.fail(lens);
        }

        var magic = magicCapability.get();
        if (!magic.isMagicUnlocked()) {
            if (level.isClientSide()) {
                player.sendSystemMessage(Component.translatable("item.arcaneadditions.soulsearchers_lens.confusion"));
            }
            return InteractionResultHolder.fail(lens);
        }

        if (!(phylacteryStack.getItem() instanceof IPhylacteryItem phylactery)) {
            return InteractionResultHolder.fail(lens);
        }

        if (phylactery.isFull(phylacteryStack)) {
            if (level.isClientSide()) {
                player.sendSystemMessage(Component.translatable("item.arcaneadditions.soulsearchers_lens.nonphylactery"));
            }
            return InteractionResultHolder.fail(lens);
        }

        if (level.isClientSide()) {
            this.PlayLoopingSound(SFX.Loops.ARCANE, player);
        } else {
            SoulSearchersBeamEntity beam = new SoulSearchersBeamEntity(EntityRegistry.SOUL_SEARCHERS_BEAM.get(), player.level());
            beam.setSource(player);
            beam.setTarget(target);
            beam.setPos(player.getEyePosition());
            lens.getOrCreateTag().putInt("beam", beam.getId());
            level.addFreshEntity(beam);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.pass(lens);
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
        int levelsRequired = (int)Math.max(1, Math.floor(adjustedHealth / ServerConfig.soulSearchersLensHealthPerLevel));

        if (target.distanceTo(player) > (float) ServerConfig.soulSearchersLensMaxDistance) {
            if (level.isClientSide) {
                player.sendSystemMessage(Component.translatable("item.arcaneadditions.soulsearchers_lens.distance"));
            }
            return false;
        }

        if (player.experienceLevel < levelsRequired && !player.isCreative()) {
            if (level.isClientSide) {
                player.sendSystemMessage(Component.translatable("item.arcaneadditions.soulsearchers_lens.experience"));
            }
            return false;
        }

        @SuppressWarnings(value="unchecked")
        EntityType<? extends Mob> type = (EntityType<? extends Mob>)target.getType();
        float amount = player.isCreative() ? ((IPhylacteryItem) phylactery.getItem()).getMaximumFill() : 1;
        boolean added = this.addToPhylactery(player, phylactery, type, amount, target.level());

        if (added) {
            player.giveExperienceLevels(-levelsRequired);
        }

        return added;
    }

    private float getAdjustmentForType(EntityType entityType) {
        String type = entityType.getDescriptionId();

        for (String mod : ServerConfig.soulSearchersLensCreatureModifiers) {
            String[] parts = mod.split(",");
            if (parts.length != 2) {
                continue;
            }

            String registryName = parts[0];
            String stringModifier = parts[1];

            if (type.equals(registryName)) {
                return Float.parseFloat(stringModifier);
            }
        }

        return 1.0f;
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
