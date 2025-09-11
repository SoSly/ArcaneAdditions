package org.sosly.arcaneadditions.entities.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

import javax.annotation.Nullable;

public abstract class AbstractFamiliarGoal extends Goal {
    protected final Mob familiar;
    protected IFamiliarCapability capability;
    
    protected AbstractFamiliarGoal(Mob familiar) {
        this.familiar = familiar;
    }
    
    @Nullable
    protected IFamiliarCapability getFamiliarCapability() {
        if (capability == null) {
            capability = FamiliarHelper.getFamiliarCapability(familiar);
        }
        return capability;
    }
    
    protected boolean hasValidCapability() {
        return getFamiliarCapability() != null;
    }
    
    @Nullable
    protected Player getCaster() {
        IFamiliarCapability cap = getFamiliarCapability();
        return cap != null ? cap.getCaster() : null;
    }
    
    protected boolean isOrderedToStay() {
        IFamiliarCapability cap = getFamiliarCapability();
        return cap != null && cap.isOrderedToStay();
    }
    
    protected boolean isBapped() {
        IFamiliarCapability cap = getFamiliarCapability();
        return cap != null && cap.isBapped();
    }
    
    @Override
    public void start() {
        super.start();
        capability = FamiliarHelper.getFamiliarCapability(familiar);
    }
    
    @Override
    public void stop() {
        super.stop();
        capability = null;
    }
}