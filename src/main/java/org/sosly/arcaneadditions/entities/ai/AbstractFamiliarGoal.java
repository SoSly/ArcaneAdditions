package org.sosly.arcaneadditions.entities.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

import javax.annotation.Nullable;

public abstract class AbstractFamiliarGoal extends Goal {
    protected final Mob familiar;
    protected final IFamiliarCapability capability;

    protected AbstractFamiliarGoal(Mob familiar) {
        this.familiar = familiar;
        this.capability = FamiliarHelper.getFamiliarCapability(familiar);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
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
