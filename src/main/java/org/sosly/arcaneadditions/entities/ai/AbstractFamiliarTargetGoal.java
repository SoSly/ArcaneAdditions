package org.sosly.arcaneadditions.entities.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

import javax.annotation.Nullable;

public abstract class AbstractFamiliarTargetGoal extends TargetGoal {
    protected final IFamiliarCapability capability;
    
    protected AbstractFamiliarTargetGoal(Mob familiar, boolean mustSee) {
        super(familiar, mustSee);
        this.capability = FamiliarHelper.getFamiliarCapability(familiar);
    }
    
    protected AbstractFamiliarTargetGoal(Mob familiar, boolean mustSee, boolean mustReach) {
        super(familiar, mustSee, mustReach);
        this.capability = FamiliarHelper.getFamiliarCapability(familiar);
    }
    
    @Override
    public void start() {
        super.start();
        System.out.println("[DEBUG] Starting target goal: " + this.getClass().getSimpleName());
    }
    
    @Override
    public void stop() {
        super.stop();
        System.out.println("[DEBUG] Stopping target goal: " + this.getClass().getSimpleName());
    }
    
    @Nullable
    protected IFamiliarCapability getFamiliarCapability() {
        return capability;
    }
    
    protected boolean hasValidCapability() {
        return capability != null;
    }
    
    @Nullable
    protected Player getCaster() {
        return capability != null ? capability.getCaster() : null;
    }
    
    protected boolean isOrderedToStay() {
        return capability != null && capability.isOrderedToStay();
    }
    
    protected boolean isBapped() {
        return capability != null && capability.isBapped();
    }
}