package org.sosly.arcaneadditions.entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class NeverTargetCasterGoal extends AbstractFamiliarTargetGoal {
    protected LivingEntity target;

    public NeverTargetCasterGoal(Mob familiar) {
        super(familiar, false);
    }

    @Override
    public boolean canUse() {
        if (!hasValidCapability() || isOrderedToStay()) {
            return false;
        }

        Player caster = getCaster();
        return caster != null && caster.is(mob.getTarget());
    }

    public void start() {
        mob.setTarget(null);
        super.start();
    }
}
