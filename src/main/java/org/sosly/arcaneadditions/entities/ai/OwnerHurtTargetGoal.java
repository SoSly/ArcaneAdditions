package org.sosly.arcaneadditions.entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

import java.util.EnumSet;

public class OwnerHurtTargetGoal extends TargetGoal {
    private final Mob familiar;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public OwnerHurtTargetGoal(Mob familiar) {
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

        ownerLastHurt = owner.getLastHurtMob();
        int i = owner.getLastHurtMobTimestamp();
        return i != timestamp && this.canAttack(ownerLastHurt, TargetingConditions.DEFAULT);
    }

    public void start() {
        familiar.setTarget(ownerLastHurt);
        Player owner = FamiliarHelper.getCaster(familiar);
        if (owner != null) {
            timestamp = owner.getLastHurtMobTimestamp();
        }

        super.start();
    }
}
