package org.sosly.arcaneadditions.entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

import java.util.EnumSet;

public class CasterHurtByTargetGoal extends TargetGoal {
    private final Mob familiar;
    private LivingEntity casterLastHurtBy;
    private int timestamp;

    public CasterHurtByTargetGoal(Mob familiar) {
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

        casterLastHurtBy = caster.getLastHurtByMob();
        int i = caster.getLastHurtByMobTimestamp();
        return i != timestamp && this.canAttack(casterLastHurtBy, TargetingConditions.DEFAULT);
    }

    public void start() {
        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(familiar);
        if (cap == null) {
            return;
        }
        Player caster = cap.getCaster();

        familiar.setTarget(casterLastHurtBy);
        if (caster != null) {
            timestamp = caster.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}
