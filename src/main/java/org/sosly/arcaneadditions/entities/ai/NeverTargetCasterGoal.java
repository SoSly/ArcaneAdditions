package org.sosly.arcaneadditions.entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

public class NeverTargetCasterGoal extends TargetGoal {
    private final Mob familiar;
    protected LivingEntity target;

    public NeverTargetCasterGoal(Mob familiar) {
        super(familiar, false);
        this.familiar = familiar;
    }

    @Override
    public boolean canUse() {
        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(familiar);
        if (cap == null || cap.isOrderedToStay()) {
            return false;
        }

        Player caster = cap.getCaster();
        return caster != null && caster.is(familiar.getTarget());
    }

    public void start() {
        familiar.setTarget(null);
        super.start();
    }
}
