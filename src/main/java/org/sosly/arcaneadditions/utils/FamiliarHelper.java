package org.sosly.arcaneadditions.utils;

import com.mna.api.capabilities.IPlayerMagic;
import com.mna.capabilities.playerdata.magic.PlayerMagicProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import org.sosly.arcaneadditions.capabilities.familiar.FamiliarProvider;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;
import org.sosly.arcaneadditions.entities.ai.*;
import org.sosly.arcaneadditions.entities.ai.Constants;

public class FamiliarHelper {
    private static final String CASTER = "arcaneadditions:familiar/caster";


    public static boolean createFamiliar(Player caster, EntityType<? extends Mob> type, Component name, Level level, BlockPos pos) {
        IFamiliarCapability cap = getFamiliarCapability(caster);
        IPlayerMagic magic = getMagicCapability(caster);
        if (cap == null || magic == null) {
            return false;
        }

        Mob familiar = type.create(level);
        if (familiar == null) {
            return false;
        }

        if (cap.getFamiliarNBT() != null) {
            familiar.load(cap.getFamiliarNBT());
            familiar.setPos(Vec3.atBottomCenterOf(pos.above()));
            familiar.setDeltaMovement(Vec3.ZERO);
            familiar.fallDistance = 0;
            familiar.clearFire();
            if (!familiar.hasCustomName()) {
                familiar.setCustomName(name);
                familiar.setCustomNameVisible(true);
            }
        } else {
            familiar.setPos(Vec3.atBottomCenterOf(pos.above()));
            familiar.setCustomName(name);
            familiar.setCustomNameVisible(true);
        }
        
        familiar.getPersistentData().putUUID(CASTER, caster.getUUID());
        setupFamiliarAI(familiar);

        if (!level.addFreshEntity(familiar)) {
            return false;
        }

        cap.setCaster(caster);
        cap.setFamiliar(familiar);
        cap.setType(type);
        cap.setName(familiar.getCustomName() != null ? familiar.getCustomName().getString() : name.getString());
        cap.getCastingResource().setMaxAmountByLevel(magic.getMagicLevel() / Constants.MAGIC_LEVEL_DIVISOR);
        if (cap.getCastingResource().getMaxAmount() < cap.getCastingResource().getAmount()) {
            cap.getCastingResource().setAmount(cap.getCastingResource().getMaxAmount());
        }
        cap.setBapped(false);
        cap.setOrderedToStay(false);
        cap.setLastInteract(level.getGameTime());

        return true;
    }

    public static Player getCaster(Mob familiar) {
        IFamiliarCapability cap = getFamiliarCapability(familiar);
        if (cap == null) {
            return null;
        }

        return cap.getCaster();
    }

    public static Mob getFamiliar(Player caster) {
        IFamiliarCapability cap = getFamiliarCapability(caster);
        if (cap == null) {
            return null;
        }

        return cap.getFamiliar();
    }

    public static IFamiliarCapability getFamiliarCapability(Mob mob) {
        if (!mob.getPersistentData().hasUUID(CASTER)) {
            return null;
        }

        Player caster = mob.level().getPlayerByUUID(mob.getPersistentData().getUUID(CASTER));
        if (caster == null) {
            mob.remove(Entity.RemovalReason.DISCARDED);
            return null;
        }

        LazyOptional<IFamiliarCapability> loCap = caster.getCapability(FamiliarProvider.FAMILIAR);
        if (!loCap.isPresent() || loCap.resolve().isEmpty()) {
            return null;
        }

        return loCap.resolve().get();
    }


    public static IFamiliarCapability getFamiliarCapability(Player caster) {
        LazyOptional<IFamiliarCapability> loCap = caster.getCapability(FamiliarProvider.FAMILIAR);
        if (!loCap.isPresent() || loCap.resolve().isEmpty()) {
            return null;
        }

        return loCap.resolve().get();
    }

    private static IPlayerMagic getMagicCapability(Player caster) {
        LazyOptional<IPlayerMagic> loCap = caster.getCapability(PlayerMagicProvider.MAGIC);
        if (!loCap.isPresent() || loCap.resolve().isEmpty()) {
            return null;
        }

        return loCap.resolve().get();
    }

    public static boolean hasFamiliar(Player caster) {
        LazyOptional<IFamiliarCapability> cap = caster.getCapability(FamiliarProvider.FAMILIAR);
        return cap.isPresent();
    }

    public static boolean isFamiliar(Mob mob) {
        return mob.getPersistentData().hasUUID(CASTER);
    }

    public static boolean isOrphaned(Mob familiar) {
        if (!familiar.getPersistentData().hasUUID(CASTER)) {
            return false;
        }

        Player caster = familiar.level().getPlayerByUUID(familiar.getPersistentData().getUUID(CASTER));
        return caster == null;
    }

    public static void removeFamiliar(Player caster) {
        IFamiliarCapability cap = getFamiliarCapability(caster);
        if (cap == null) {
            return;
        }

        if (cap.getFamiliar() != null) {
            cap.storeFamiliarData();
            cap.getFamiliar().remove(Entity.RemovalReason.DISCARDED);
            caster.sendSystemMessage(Component.translatable("arcaneadditions:rituals/bind_familiar.already_bound"));
        }

        cap.reset();
    }

    public static void setupFamiliarAI(Mob familiar) {
        familiar.removeFreeWill();

        // Add new goals
        familiar.goalSelector.addGoal(2, new StayWhenOrderedToGoal(familiar));
        familiar.goalSelector.addGoal(5, new CastOffensiveSpell(familiar, Constants.CAST_DISTANCE_SQUARED));
        familiar.goalSelector.addGoal(6, new FollowCasterGoal(familiar, 1.0D, 20, 2.0F, 16, false));
        familiar.goalSelector.addGoal(7, new CastUtilitySpell(familiar, Constants.CAST_DISTANCE_SQUARED));
        familiar.goalSelector.addGoal(10, new RandomWanderGoal(familiar, 1.0D, 20));

        familiar.targetSelector.addGoal(0, new NeverTargetCasterGoal(familiar));
        familiar.targetSelector.addGoal(1, new CasterHurtByTargetGoal(familiar));
        familiar.targetSelector.addGoal(2, new CasterHurtTargetGoal(familiar));
    }

    public static int calculateSpellcastingProbability(int initialProbability, int elapsedSeconds) {
        double linearStart = initialProbability * 0.75;
        double exponentialEnd = initialProbability * 1.25;

        // Constants for adjustments (these may need fine-tuning)
        double linearEndProbability = initialProbability / 1.5;
        double linearRate = (initialProbability - linearEndProbability) / (initialProbability - linearStart);
        double k = 0.2; // Exponential growth rate

        if (elapsedSeconds < linearStart) {
            // Phase 1: Constant probability
            return initialProbability;
        } else if (elapsedSeconds < initialProbability) {
            // Phase 2: Linear increase
            return (int) (initialProbability - linearRate * (elapsedSeconds - linearStart));
        } else if (elapsedSeconds <= exponentialEnd) {
            // Phase 3: Exponential increase
            double startProbPhase3 = initialProbability - linearRate * (initialProbability - linearStart);
            return (int) (startProbPhase3 / Math.exp(k * (elapsedSeconds - initialProbability)));
        } else {
            // Beyond the designed curve, assuming certainty
            return 1;
        }
    }
}
