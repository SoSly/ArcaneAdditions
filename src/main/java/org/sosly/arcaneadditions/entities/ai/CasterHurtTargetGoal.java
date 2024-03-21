package org.sosly.arcaneadditions.entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

import java.util.EnumSet;

public class CasterHurtTargetGoal extends TargetGoal {
    private final Mob familiar;
    private LivingEntity casterLastHurt;
    private int timestamp;

    public CasterHurtTargetGoal(Mob familiar) {
        super(familiar, false);
        this.familiar = familiar;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(familiar);
        if (cap == null || cap.isOrderedToStay()) {
            return false;
        }

        Player caster = cap.getCaster();
        if (caster == null) {
            return false;
        }

        casterLastHurt = caster.getLastHurtMob();
        int i = caster.getLastHurtMobTimestamp();
        return i != timestamp && this.canAttack(casterLastHurt, TargetingConditions.DEFAULT);
    }

    public void start() {
        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(familiar);
        if (cap == null) {
            return;
        }

        familiar.setTarget(casterLastHurt);
        Player caster = cap.getCaster();
        if (caster != null) {
            timestamp = caster.getLastHurtMobTimestamp();
        }

        super.start();
    }
}
