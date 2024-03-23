package org.sosly.arcaneadditions.entities.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.sosly.arcaneadditions.capabilities.familiar.IFamiliarCapability;
import org.sosly.arcaneadditions.utils.FamiliarHelper;

import java.util.EnumSet;

public class FollowCasterGoal extends Goal {
    private final Mob familiar;
    private final LevelReader level;
    private final double followSpeed;
    private final PathNavigation navigator;
    private int timeToRecalcPath;
    private final float pathDist;
    private final float attackDist;
    private final float snapDist;
    private float oldWaterCost;
    private final boolean teleportToLeaves;

    public FollowCasterGoal(Mob familiar, double speed, float pathDist, float attackDist, float snapDist, boolean teleportToLeaves) {
        this.familiar = familiar;
        this.level = familiar.level();
        this.followSpeed = speed;
        this.navigator = familiar.getNavigation();
        this.pathDist = pathDist;
        this.attackDist = attackDist;
        this.snapDist = snapDist;
        this.teleportToLeaves = teleportToLeaves;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(familiar.getNavigation() instanceof GroundPathNavigation) && !(familiar.getNavigation() instanceof FlyingPathNavigation) && !(familiar.getNavigation() instanceof WaterBoundPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canUse() {
        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(familiar);
        if (cap == null || cap.isOrderedToStay()) {
            return false;
        }

        Player caster = cap.getCaster();
        if (caster == null) {
            return false;
        }

        if (familiar.distanceToSqr(caster) < (double)(snapDist * snapDist)) {
            return false;
        }

        if (familiar.getTarget() != null && familiar.distanceToSqr(caster) < (double)(attackDist * attackDist)) {
            return false;
        }

        familiar.setTarget(null);
        return true;
    }

    public boolean canContinueToUse() {
        return !navigator.isDone();
    }

    public void start() {
        timeToRecalcPath = 0;
        oldWaterCost = familiar.getPathfindingMalus(BlockPathTypes.WATER);
        familiar.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public void stop() {
        navigator.stop();
        familiar.setPathfindingMalus(BlockPathTypes.WATER, oldWaterCost);
    }

    public void tick() {
        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(familiar);
        if (cap == null) {
            return;
        }

        Player caster = cap.getCaster();
        if (caster == null) {
            return;
        }

        familiar.getLookControl().setLookAt(caster, 10.0F, (float) familiar.getMaxHeadXRot());
        if (--timeToRecalcPath <= 0) {
            timeToRecalcPath = 10;
            if (!familiar.isLeashed() && !familiar.isPassenger()) {
                if (familiar.distanceToSqr(caster) >= (double)(pathDist * pathDist)) {
                    tryToTeleportNearEntity();
                } else {
                    navigator.moveTo(caster, followSpeed);
                }
            }
        }
    }

    private void tryToTeleportNearEntity() {
        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(familiar);
        if (cap == null) {
            return;
        }

        Player caster = cap.getCaster();
        if (caster == null) {
            return;
        }

        BlockPos blockpos = caster.blockPosition();

        for(int i = 0; i < 10; ++i) {
            int j = getRandomNumber(-3, 3);
            int k = getRandomNumber(-1, 1);
            int l = getRandomNumber(-3, 3);
            boolean flag = tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
            if (flag) {
                return;
            }
        }
    }

    private boolean tryToTeleportToLocation(int x, int y, int z) {
        IFamiliarCapability cap = FamiliarHelper.getFamiliarCapability(familiar);
        if (cap == null) {
            return false;
        }

        Player caster = cap.getCaster();
        if (caster == null) {
            return false;
        }

        if (Math.abs((double)x - caster.getX()) < 2.0 && Math.abs((double)z - caster.getZ()) < 2.0) {
            return false;
        }
        if (!isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
            return false;
        }

        familiar.moveTo((double)x + 0.5, (double)y, (double)z + 0.5, familiar.getYRot(), familiar.getXRot());
        navigator.stop();
        return true;
    }

    private boolean isTeleportFriendlyBlock(BlockPos pos) {
        BlockPathTypes pathnodetype = WalkNodeEvaluator.getBlockPathTypeStatic(level, pos.mutable());
        if (pathnodetype != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            BlockState blockstate = level.getBlockState(pos.below());
            if (!teleportToLeaves && blockstate.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockpos = pos.subtract(this.familiar.blockPosition());
                return level.noCollision(familiar, familiar.getBoundingBox().move(blockpos));
            }
        }
    }

    private int getRandomNumber(int min, int max) {
        return familiar.getRandom().nextInt(max - min + 1) + min;
    }
}
