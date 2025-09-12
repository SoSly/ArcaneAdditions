package org.sosly.arcaneadditions.entities.ai;

import com.mna.api.ManaAndArtificeMod;
import com.mna.api.spells.attributes.Attribute;
import com.mna.api.spells.parts.Shape;
import com.mna.api.spells.targeting.SpellSource;
import com.mna.api.spells.targeting.SpellTarget;
import com.mna.spells.shapes.ShapeSelf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.sosly.arcaneadditions.config.ServerConfig;
import org.sosly.arcaneadditions.spells.FamiliarSpell;
import org.sosly.arcaneadditions.spells.shapes.FamiliarShape;
import org.sosly.arcaneadditions.spells.shapes.SharedShape;

import java.util.EnumSet;
import java.util.Optional;

public class CastOffensiveSpell extends AbstractFamiliarGoal {
    private final float maxCastDistance;
    private boolean hasCast;
    private boolean cannotCast;
    private double distance;
    private long lastAttempt;
    private Optional<FamiliarSpell> spellToCast = Optional.empty();
    private LivingEntity target;
    private int seeTime;
    private boolean strafingBackwards;
    private boolean strafingClockwise;
    private int strafingTime;

    public CastOffensiveSpell(Mob familiar, float maxCastDistance) {
        super(familiar);
        this.lastAttempt = -1;
        this.distance = 0;
        this.strafingTime = -1;
        this.hasCast = false;
        this.maxCastDistance = maxCastDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!hasValidCapability() || isOrderedToStay() || isBapped()) {
            return false;
        }
        if (capability.getSpellsKnown().isEmpty()) {
            return false;
        }
        if (familiar.getTarget() == null) {
            return false;
        }

        long currentTime = familiar.getServer().overworld().getGameTime();
        boolean hasAvailableSpell = capability.getSpellsKnown().stream()
                .filter(FamiliarSpell::isOffensive)
                .anyMatch(spell -> {
                    long sinceLastCast = currentTime - spell.getLastCast();
                    return sinceLastCast >= spell.getFrequency().getSeconds() * 20;
                });

        if (!hasAvailableSpell) {
            return false;
        }

        long sinceLastAttempt = currentTime - lastAttempt;
        if (sinceLastAttempt < ServerConfig.spellCastAttemptCooldown) {
            return false;
        }

        return hasAvailableSpell;
    }

    public boolean canContinueToUse() {
        if (this.hasCast) {
            return false;
        }

        if (!hasValidCapability() || isOrderedToStay()) {
            return false;
        }

        if (!spellToCast.isPresent()) {
            return false;
        }

        float mana = spellToCast.get().getRecipe().getManaCost();
        if (capability.getCastingResource().getAmount() < mana + ServerConfig.spellMinimumManaBuffer) {
            return false;
        }

        if (target == null || target.isRemoved()) {
            return false;
        }

        return true;
    }


    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        super.start();
        hasCast = false;
        lastAttempt = familiar.getServer().overworld().getGameTime();

        selectSpellToCast();

        if (!spellToCast.isPresent()) {
            this.cannotCast = true;
            return;
        }

        FamiliarSpell spell = spellToCast.get();
        if (spell.getRecipe().getShape() == null) {
            return;
        }

        Shape shape = spell.getRecipe().getShape().getPart();

        if (shape instanceof FamiliarShape || shape instanceof SharedShape || shape instanceof ShapeSelf) {
            target = familiar;
            distance = 0;
            return;
        }

        if (!hasValidCapability()) {
            return;
        }

        target = familiar.getTarget();

        distance = switch (shape.getClass().getSimpleName()) {
            case "ShapeProjectile" -> maxCastDistance;
            case "ShapeBeam", "ShapeCone" -> Math.min(8.0, maxCastDistance);
            default -> Math.max(spell.getRecipe().getShape().getValue(Attribute.RANGE), Constants.DEFAULT_MIN_ATTRIBUTE_RANGE);
        };
    }

    @Override
    public void stop() {
        super.stop();
        this.hasCast = false;
        this.cannotCast = false;
        this.target = null;
        this.spellToCast = Optional.empty();
        this.seeTime = 0;
        this.strafingTime = -1;
        this.distance = 0;
    }

    @Override
    public void tick() {
        if (target.equals(familiar)) {
            castSpellOnSelf();
            return;
        }

        double distanceToTarget = familiar.distanceToSqr(target.getX(), target.getY(), target.getZ());
        updateSeeTime();

        if (shouldStrafe(distanceToTarget)) {
            familiar.getNavigation().stop();
            ++this.strafingTime;
        } else {
            familiar.getNavigation().moveTo(target, ServerConfig.spellNavigationSpeed);
            this.strafingTime = -1;
        }

        updateStrafingBehavior(distanceToTarget);

        if (distanceToTarget > distance) {
            return;
        }

        familiar.getNavigation().stop();
        familiar.getLookControl().setLookAt(target, (float)ServerConfig.spellLookAtSpeed, (float)ServerConfig.spellLookAtSpeed);
        familiar.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

        ManaAndArtificeMod.getSpellHelper()
                .affect(spellToCast.get().getRecipe().createAsSpell(), spellToCast.get().getRecipe(),
                        familiar.level(), new SpellSource(familiar, InteractionHand.MAIN_HAND), new SpellTarget(target));

        float mana = spellToCast.get().getRecipe().getManaCost();
        capability.getCastingResource().consume(familiar, mana);
        this.hasCast = true;
        this.spellToCast.get().setLastCast(familiar.getServer().overworld().getGameTime());
    }

    private void castSpellOnSelf() {
        familiar.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        ManaAndArtificeMod.getSpellHelper()
                .affect(spellToCast.get().getRecipe().createAsSpell(), spellToCast.get().getRecipe(),
                        familiar.level(), new SpellSource(familiar, InteractionHand.MAIN_HAND), new SpellTarget(familiar));
        this.hasCast = true;
    }

    private void selectSpellToCast() {
        long currentTime = familiar.getServer().overworld().getGameTime();

        spellToCast = capability.getSpellsKnown().stream()
                .filter(FamiliarSpell::isOffensive)
                .filter(spell -> {
                    long sinceLastCast = currentTime - spell.getLastCast();
                    return sinceLastCast >= spell.getFrequency().getSeconds() * 20;
                })
                .findAny();
    }

    private void updateSeeTime() {
        boolean canSee = familiar.getSensing().hasLineOfSight(target);
        boolean positiveSeeTime = this.seeTime > 0;

        if (canSee != positiveSeeTime) {
            this.seeTime = 0;
        }

        if (canSee) {
            ++this.seeTime;
        } else {
            --this.seeTime;
        }
    }

    private boolean shouldStrafe(double distanceToTarget) {
        return distanceToTarget <= distance
            && distanceToTarget <= maxCastDistance
            && seeTime >= ServerConfig.spellSightRequiredTime;
    }

    private void updateStrafingBehavior(double distanceToTarget) {
        if (this.strafingTime >= ServerConfig.spellStrafingRecalcTime) {
            recalculateStrafingDirection();
            strafingTime = 0;
        }

        if (strafingTime < -1) {
            familiar.getLookControl().setLookAt(target, (float)ServerConfig.spellLookAtSpeed, (float)ServerConfig.spellLookAtSpeed);
            return;
        }

        adjustStrafingDistance(distanceToTarget);
        performStrafing();
    }

    private void recalculateStrafingDirection() {
        if (familiar.getRandom().nextFloat() < ServerConfig.spellStrafingChance) {
            strafingClockwise = !strafingClockwise;
        }
        if (familiar.getRandom().nextFloat() < ServerConfig.spellStrafingChance) {
            strafingBackwards = !strafingBackwards;
        }
    }

    private void adjustStrafingDistance(double distanceToTarget) {
        if (distanceToTarget > distance * ServerConfig.spellStrafingBackwardThreshold) {
            strafingBackwards = false;
            return;
        }

        if (distanceToTarget < distance * ServerConfig.spellStrafingForwardThreshold) {
            strafingBackwards = true;
        }
    }

    private void performStrafing() {
        float forward = strafingBackwards ? Constants.STRAFE_BACKWARD_SPEED : Constants.STRAFE_FORWARD_SPEED;
        float strafe = strafingClockwise ? Constants.STRAFE_RIGHT_SPEED : Constants.STRAFE_LEFT_SPEED;

        familiar.getMoveControl().strafe(
            forward * (float)ServerConfig.spellStrafingSpeed,
            strafe * (float)ServerConfig.spellStrafingSpeed
        );
        familiar.lookAt(target, (float)ServerConfig.spellLookAtSpeed, (float)ServerConfig.spellLookAtSpeed);
    }
}
