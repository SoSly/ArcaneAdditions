package org.sosly.arcaneadditions.entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

import java.util.EnumSet;

public class StayWhenOrderedToGoal extends Goal {
    private final Mob familiar;

    public StayWhenOrderedToGoal(Mob mob) {
        this.familiar = mob;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    public boolean canContinueToUse() {
        return FamiliarHelper.isOrderedToStay(familiar);
    }

    public boolean canUse() {
        if (!FamiliarHelper.isFamiliar(familiar)) {
            return false;
        }

        if (familiar.isInWaterOrBubble()) {
            return false;
        }

        Player owner = FamiliarHelper.getCaster(familiar);
        if (owner == null) {
            return false;
        }

        return (!(familiar.distanceToSqr(owner) < 144.0D) || owner.getLastHurtByMob() == null) && FamiliarHelper.isOrderedToStay(familiar);
    }

    public void start() {
        familiar.getNavigation().stop();
        FamiliarHelper.setInStayingPose(familiar, true);
    }

    public void stop() {
        FamiliarHelper.setInStayingPose(familiar, false);
    }
}
