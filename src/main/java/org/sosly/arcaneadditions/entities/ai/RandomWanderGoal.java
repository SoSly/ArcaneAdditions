package org.sosly.arcaneadditions.entities.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class RandomWanderGoal extends AbstractFamiliarGoal {
    private final double speed;
    private final int interval;

    private double wantedX;
    private double wantedY;
    private double wantedZ;

    public RandomWanderGoal(Mob familiar, double speed, int interval) {
        super(familiar);
        this.speed = speed;
        this.interval = interval;

        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (familiar.isVehicle()) {
            return false;
        }

        if (familiar.getNoActionTime() >= 100) {
            return false;
        }

        if (familiar.getRandom().nextInt(reducedTickDelay(interval)) != 0) {
            return false;
        }

        Vec3 vec3 = getWanderVector(10, 7);
        if (vec3 == null) {
            return false;
        }

        wantedX = vec3.x;
        wantedY = vec3.y;
        wantedZ = vec3.z;
        return true;
    }

    public boolean canContinueToUse() {
        return !familiar.getNavigation().isDone() && !familiar.isVehicle();
    }

    @Override
    public void start() {
        familiar.getNavigation().moveTo(wantedX, wantedY, wantedZ, speed);
    }

    @Override
    public void stop() {
        familiar.getNavigation().stop();
        super.stop();
    }

    @Nullable
    private Vec3 getWanderVector(int radius, int verticalDistance) {
        double d0 = Double.NEGATIVE_INFINITY;
        BlockPos blockpos = null;
        for (int i = 0; i < 10; ++i) {
            BlockPos pos = getWanderTarget(radius, verticalDistance);
            if (pos != null) {
                double d1 = 0.0D;
                if (familiar instanceof PathfinderMob pFamiliar) {
                    pFamiliar.getWalkTargetValue(pos);
                }

                if (d1 > d0) {
                    d0 = d1;
                    blockpos = pos;
                }
            }
        }

        return blockpos != null ? Vec3.atBottomCenterOf(blockpos) : null;
    }

    @Nullable
    private BlockPos getWanderTarget(int radius, int verticalDistance) {
        BlockPos goal = RandomPos.generateRandomDirection(familiar.getRandom(), radius, verticalDistance);
        Player caster = getCaster();
        BlockPos casterPos = caster != null ? caster.blockPosition() : null;
        int goalX = goal.getX();
        int goalZ = goal.getZ();

        if (familiar.hasRestriction() && radius > 1) {
            BlockPos blockpos = familiar.getRestrictCenter();
            int distX = familiar.getRandom().nextInt(radius / 2);
            int distZ = familiar.getRandom().nextInt(radius / 2);
            goalX += familiar.getX() > blockpos.getX() ? -distX : distX;
            goalZ += familiar.getZ() > blockpos.getZ() ? -distZ : distZ;
        }

        BlockPos target = BlockPos.containing((double) goalX + familiar.getX(), (double) goal.getY() + familiar.getY(), (double) goalZ + familiar.getZ());
        if (isOutsideLimits(target)) {
            return casterPos;
        }

        if (isRestricted(familiar.hasRestriction(), target)) {
            return null;
        }

        if (needsStability() && !familiar.getNavigation().isStableDestination(target)) {
            return casterPos;
        }

        if (familiar.getPathfindingMalus(WalkNodeEvaluator.getBlockPathTypeStatic(familiar.level(), target.mutable())) != 0.0F) {
            return casterPos;
        }

        return target;
    }

    private boolean needsStability() {
        return !(familiar.getMoveControl() instanceof FlyingMoveControl);
    }

    private boolean isOutsideLimits(BlockPos pos) {
        return pos.getY() < familiar.level().getMinBuildHeight() || pos.getY() > familiar.level().getMaxBuildHeight();
    }

    private boolean isRestricted(boolean shortCircuit, BlockPos pos) {
        return shortCircuit && familiar.isWithinRestriction(pos);
    }
}
