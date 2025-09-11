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
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;
import org.sosly.arcaneadditions.spells.FamiliarSpell;
import org.sosly.arcaneadditions.spells.shapes.FamiliarShape;
import org.sosly.arcaneadditions.spells.shapes.SharedShape;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

import java.util.EnumSet;
import java.util.Optional;

public class CastUtilitySpell extends Goal {
    private final Mob familiar;
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

    public CastUtilitySpell(Mob familiar, float maxCastDistance) {
        this.lastAttempt = -1;
        this.distance = 0;
        this.strafingTime = -1;
        this.hasCast = false;
        this.familiar = familiar;
        this.maxCastDistance = maxCastDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(familiar);
        if (cap == null || cap.isOrderedToStay() || cap.isBapped()) {
            return false;
        }
        if (cap.getSpellsKnown().isEmpty()) {
            return false;
        }
        long sinceLastAttempt = familiar.getServer().overworld().getGameTime() - lastAttempt;
        if (sinceLastAttempt < 20L) {
            return false;
        }
        lastAttempt = familiar.getServer().overworld().getGameTime();
        spellToCast = cap.getSpellsKnown().stream()
                .filter(spell -> !spell.isOffensive())
                .filter(spell -> {
                    int sinceLastCast = (int) ((familiar.getServer().overworld().getGameTime() - spell.getLastCast())/20);
                    if (sinceLastCast < (spell.getFrequency().getSeconds()/4)) {
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
            IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(familiar);
            if (cap == null) {
                return;
            }
            target = cap.getCaster();
            distance = maxCastDistance;
        } else {
            IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(familiar);
            if (cap == null) {
                return;
            }
            target = cap.getCaster();
            distance = Math.max(spellToCast.get().getRecipe().getShape().getValue(Attribute.RANGE), 4);
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.hasCast = false;
        this.cannotCast = false;
    }

    @Override
    public void tick() {
        if (!this.canContinueToUse()) {
            this.stop();
            return;
        }

        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(familiar);
        if (cap == null || cap.isOrderedToStay()) {
            this.cannotCast = true;
            return;
        }

        float mana = spellToCast.get().getRecipe().getManaCost();
        if (cap.getCastingResource().getAmount() < mana + 10) {
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

        if (!(distanceToTarget > distance || distanceToTarget > maxCastDistance) && seeTime >= 20) {
            familiar.getNavigation().stop();
            ++this.strafingTime;
        } else {
            familiar.getNavigation().moveTo(target, 1.0D);
            this.strafingTime = -1;
        }

        if (this.strafingTime >= 20) {
            if ((double)familiar.getRandom().nextFloat() < 0.3D) {
                strafingClockwise = !strafingClockwise;
            }

            if ((double)familiar.getRandom().nextFloat() < 0.3D) {
                strafingBackwards = !strafingBackwards;
            }

            strafingTime = 0;
        }

        if (strafingTime >= -1) {
            if (distanceToTarget > distance * 0.75) {
                strafingBackwards = false;
            } else if (distanceToTarget < distance * 0.25) {
                strafingBackwards = true;
            }

            familiar.getMoveControl().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
            familiar.lookAt(target, 30.0F, 30.0F);
        } else {
            familiar.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        if (distanceToTarget <= distance) {
            familiar.getNavigation().stop();
            familiar.getLookControl().setLookAt(target, 30.0F, 30.0F);
            familiar.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            ManaAndArtificeMod.getSpellHelper()
                    .affect(spellToCast.get().getRecipe().createAsSpell(), spellToCast.get().getRecipe(),
                            familiar.level(), new SpellSource(familiar, InteractionHand.MAIN_HAND), new SpellTarget(target));
            cap.getCastingResource().consume(familiar, mana);
            this.hasCast = true;
            this.spellToCast.get().setLastCast(familiar.getServer().overworld().getGameTime());
            this.stop();
        }
    }
}
