package org.sosly.arcaneadditions.entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

public class NeverTargetOwnerGoal extends TargetGoal {
    private final Mob familiar;
    protected LivingEntity target;

    public NeverTargetOwnerGoal(Mob familiar) {
        super(familiar, false);
        this.familiar = familiar;
    }

    @Override
    public boolean canUse() {
        if (!FamiliarHelper.isFamiliar(familiar)) {
            return false;
        }

        if (familiar.getTarget() == null) {
            return false;
        }

        Player owner = FamiliarHelper.getCaster(familiar);
        return owner != null && owner.is(familiar.getTarget());
    }

    public void start() {
        familiar.setTarget(null);
        super.start();
    }
}
