package org.sosly.arcaneadditions.entities.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

import java.util.EnumSet;

public class StayWhenOrderedToGoal extends Goal {
    private final Mob familiar;

    public StayWhenOrderedToGoal(Mob mob) {
        this.familiar = mob;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    public boolean canContinueToUse() {
        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(familiar);
        return cap != null && cap.isOrderedToStay();
    }

    public boolean canUse() {
        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(familiar);
        if (cap == null) {
            return false;
        }

        if (familiar.isInWaterOrBubble()) {
            return false;
        }

        Player caster = cap.getCaster();
        if (caster == null) {
            return false;
        }

        return (!(familiar.distanceToSqr(caster) < 144.0D) || caster.getLastHurtByMob() == null) && cap.isOrderedToStay();
    }

    public void start() {
        familiar.getNavigation().stop();
        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(familiar);
        if (cap != null) {
            cap.setOrderedToStay(true);
        }
    }

    public void stop() {
        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(familiar);
        if (cap != null) {
            cap.setOrderedToStay(false);
        }
    }
}
