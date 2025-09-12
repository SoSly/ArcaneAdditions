package org.sosly.arcaneadditions.entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class CasterHurtTargetGoal extends AbstractFamiliarTargetGoal {
    private LivingEntity casterLastHurt;
    private int timestamp;

    public CasterHurtTargetGoal(Mob familiar) {
        super(familiar, false);
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if (!hasValidCapability() || isOrderedToStay()) {
            return false;
        }

        Player caster = getCaster();
        if (caster == null) {
            return false;
        }

        casterLastHurt = caster.getLastHurtMob();
        int i = caster.getLastHurtMobTimestamp();
        return i != timestamp && this.canAttack(casterLastHurt, TargetingConditions.DEFAULT);
    }

    public void start() {
        mob.setTarget(casterLastHurt);
        Player caster = getCaster();
        if (caster != null) {
            timestamp = caster.getLastHurtMobTimestamp();
        }

        super.start();
    }
}
