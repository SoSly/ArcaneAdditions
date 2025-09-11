package org.sosly.arcaneadditions.entities.ai;

import com.mna.api.ManaAndArtificeMod;
import com.mna.api.spells.attributes.Attribute;
import com.mna.api.spells.parts.Shape;
import com.mna.api.spells.targeting.SpellSource;
import com.mna.api.spells.targeting.SpellTarget;
import com.mna.spells.shapes.ShapeProjectile;
import com.mna.spells.shapes.ShapeSelf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.sosly.arcaneadditions.config.ServerConfig;
import org.sosly.arcaneadditions.entities.ai.config.FamiliarAIConfig;
import org.sosly.arcaneadditions.spells.FamiliarSpell;
import org.sosly.arcaneadditions.spells.shapes.FamiliarShape;
import org.sosly.arcaneadditions.spells.shapes.SharedShape;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

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
        long sinceLastAttempt = familiar.getServer().overworld().getGameTime() - lastAttempt;
        if (sinceLastAttempt < ServerConfig.spellCastAttemptCooldown) {
            return false;
        }
        lastAttempt = familiar.getServer().overworld().getGameTime();
        spellToCast = capability.getSpellsKnown().stream()
                .filter(FamiliarSpell::isOffensive)
                .filter(spell -> {
                    int sinceLastCast = (int) ((familiar.getServer().overworld().getGameTime() - spell.getLastCast()) / FamiliarAIConfig.TICKS_PER_SECOND);
                    if (sinceLastCast < (spell.getFrequency().getSeconds() / FamiliarAIConfig.SPELL_FREQUENCY_DIVISOR)) {
                        return false;
                    }
                    int possibility = Math.max(FamiliarHelper.calculateSpellcastingProbability(spell.getFrequency().getSeconds(), sinceLastCast), 1);
                    int random = familiar.getServer().overworld().getRandom().nextInt(possibility);
                    return random == 0;
                })
                .findAny();
        return spellToCast.isPresent();
    }

    public boolean canContinueToUse() {
        return !this.hasCast && !this.cannotCast;
    }


    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        super.start();

        hasCast = false;
        FamiliarSpell spell = spellToCast.get();
        if (spell.getRecipe().getShape() == null) {
            return;
        }
        Shape shape = spell.getRecipe().getShape().getPart();
        if (shape instanceof FamiliarShape || shape instanceof SharedShape || shape instanceof ShapeSelf) {
            target = familiar;
            distance = 0;
        } else if (shape instanceof ShapeProjectile) {
            if (!hasValidCapability()) {
                return;
            }
            target = familiar.getTarget();
            distance = maxCastDistance;
        } else {
            if (!hasValidCapability()) {
                return;
            }
            target = familiar.getTarget();
            distance = Math.max(spellToCast.get().getRecipe().getShape().getValue(Attribute.RANGE), FamiliarAIConfig.DEFAULT_MIN_ATTRIBUTE_RANGE);
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.hasCast = false;
        this.cannotCast = false;
        this.target = null;
    }

    @Override
    public void tick() {
        if (!this.canContinueToUse()) {
            this.stop();
            return;
        }

        if (!hasValidCapability() || isOrderedToStay()) {
            this.cannotCast = true;
            return;
        }

        float mana = spellToCast.get().getRecipe().getManaCost();
        if (capability.getCastingResource().getAmount() < mana + ServerConfig.spellMinimumManaBuffer) {
            this.cannotCast = true;
            return;
        }

        if (target == null || target.isRemoved()) {
            this.cannotCast = true;
            return;
        }

        if (target.equals(familiar)) {
            familiar.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            ManaAndArtificeMod.getSpellHelper()
                    .affect(spellToCast.get().getRecipe().createAsSpell(), spellToCast.get().getRecipe(),
                            familiar.level(), new SpellSource(familiar, InteractionHand.MAIN_HAND), new SpellTarget(familiar));
            this.hasCast = true;
            return;
        }

        double distanceToTarget = familiar.distanceToSqr(target.getX(), target.getY(), target.getZ());
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

        if (!(distanceToTarget > distance || distanceToTarget > maxCastDistance) && seeTime >= ServerConfig.spellSightRequiredTime) {
            familiar.getNavigation().stop();
            ++this.strafingTime;
        } else {
            familiar.getNavigation().moveTo(target, ServerConfig.spellNavigationSpeed);
            this.strafingTime = -1;
        }

        if (this.strafingTime >= ServerConfig.spellStrafingRecalcTime) {
            if ((double)familiar.getRandom().nextFloat() < ServerConfig.spellStrafingChance) {
                strafingClockwise = !strafingClockwise;
            }

            if ((double)familiar.getRandom().nextFloat() < ServerConfig.spellStrafingChance) {
                strafingBackwards = !strafingBackwards;
            }

            strafingTime = 0;
        }

        if (strafingTime >= -1) {
            if (distanceToTarget > distance * ServerConfig.spellStrafingBackwardThreshold) {
                strafingBackwards = false;
            } else if (distanceToTarget < distance * ServerConfig.spellStrafingForwardThreshold) {
                strafingBackwards = true;
            }

            float forward = strafingBackwards ? FamiliarAIConfig.STRAFE_BACKWARD_SPEED : FamiliarAIConfig.STRAFE_FORWARD_SPEED;
            float strafe = strafingClockwise ? FamiliarAIConfig.STRAFE_RIGHT_SPEED : FamiliarAIConfig.STRAFE_LEFT_SPEED;
            familiar.getMoveControl().strafe(forward * (float)ServerConfig.spellStrafingSpeed, strafe * (float)ServerConfig.spellStrafingSpeed);
            familiar.lookAt(target, (float)ServerConfig.spellLookAtSpeed, (float)ServerConfig.spellLookAtSpeed);
        } else {
            familiar.getLookControl().setLookAt(target, (float)ServerConfig.spellLookAtSpeed, (float)ServerConfig.spellLookAtSpeed);
        }

        if (distanceToTarget <= distance) {
            familiar.getNavigation().stop();
            familiar.getLookControl().setLookAt(target, (float)ServerConfig.spellLookAtSpeed, (float)ServerConfig.spellLookAtSpeed);
            familiar.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            ManaAndArtificeMod.getSpellHelper()
                    .affect(spellToCast.get().getRecipe().createAsSpell(), spellToCast.get().getRecipe(),
                            familiar.level(), new SpellSource(familiar, InteractionHand.MAIN_HAND), new SpellTarget(target));
            capability.getCastingResource().consume(familiar, mana);
            this.hasCast = true;
            this.spellToCast.get().setLastCast(familiar.getServer().overworld().getGameTime());
            this.stop();
        }
    }
}
