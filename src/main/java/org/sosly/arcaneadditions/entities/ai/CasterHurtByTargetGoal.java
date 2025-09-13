package org.sosly.arcaneadditions.entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class CasterHurtByTargetGoal extends AbstractFamiliarTargetGoal {
    private LivingEntity casterLastHurtBy;
    private int timestamp;

    public CasterHurtByTargetGoal(Mob familiar) {
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

        casterLastHurtBy = caster.getLastHurtByMob();
        int i = caster.getLastHurtByMobTimestamp();
        return i != timestamp && this.canAttack(casterLastHurtBy, TargetingConditions.DEFAULT);
    }

    public void start() {
        mob.setTarget(casterLastHurtBy);
        Player caster = getCaster();
        if (caster != null) {
            timestamp = caster.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}
