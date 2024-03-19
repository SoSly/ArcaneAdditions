package org.sosly.arcaneadditions.entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

import java.util.EnumSet;

public class OwnerHurtByTargetGoal extends TargetGoal {
    private final Mob familiar;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public OwnerHurtByTargetGoal(Mob familiar) {
        super(familiar, false);
        this.familiar = familiar;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if (!FamiliarHelper.isFamiliar(familiar)) {
            return false;
        }

        if (FamiliarHelper.isOrderedToStay(familiar)) {
            return false;
        }

        Player owner = FamiliarHelper.getCaster(familiar);
        if (owner == null) {
            return false;
        }

        ownerLastHurtBy = owner.getLastHurtByMob();
        int i = owner.getLastHurtByMobTimestamp();
        return i != timestamp && this.canAttack(ownerLastHurtBy, TargetingConditions.DEFAULT);
    }

    public void start() {
        familiar.setTarget(ownerLastHurtBy);
        Player owner = FamiliarHelper.getCaster(familiar);
        if (owner != null) {
            timestamp = owner.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}
