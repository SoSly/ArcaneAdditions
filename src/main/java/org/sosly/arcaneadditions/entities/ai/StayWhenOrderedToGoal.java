package org.sosly.arcaneadditions.entities.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.sosly.arcaneadditions.config.ServerConfig;

import java.util.EnumSet;

public class StayWhenOrderedToGoal extends AbstractFamiliarGoal {
    public StayWhenOrderedToGoal(Mob mob) {
        super(mob);
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    public boolean canContinueToUse() {
        return hasValidCapability() && isOrderedToStay();
    }

    public boolean canUse() {
        if (!hasValidCapability()) {
            return false;
        }

        if (familiar.isInWaterOrBubble()) {
            return false;
        }

        Player caster = getCaster();
        if (caster == null) {
            return false;
        }

        return (!(familiar.distanceToSqr(caster) < ServerConfig.familiarStayDistanceThreshold) || caster.getLastHurtByMob() == null) && isOrderedToStay();
    }

    public void start() {
        super.start();
        familiar.getMoveControl().setWantedPosition(familiar.getX(), familiar.getY(), familiar.getZ(), 0.0D);
        familiar.getNavigation().stop();
        if (hasValidCapability()) {
            capability.setOrderedToStay(true);
        }
    }

    public void stop() {
        if (hasValidCapability()) {
            capability.setOrderedToStay(false);
        }
        super.stop();
    }
}
