package org.sosly.arcaneadditions.entities.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
import org.sosly.arcaneadditions.utils.FamiliarHelper;

import java.util.EnumSet;

public class FollowOwnerGoal extends Goal {
    private final Mob mob;
    private LivingEntity owner;
    private final LevelReader level;
    private final double followSpeed;
    private final PathNavigation navigator;
    private int timeToRecalcPath;
    private final float pathDist;
    private final float attackDist;
    private final float snapDist;
    private float oldWaterCost;
    private final boolean teleportToLeaves;

    public FollowOwnerGoal(Mob mob, double speed, float pathDist, float attackDist, float snapDist, boolean teleportToLeaves) {
        this.mob = mob;
        this.level = mob.level();
        this.followSpeed = speed;
        this.navigator = mob.getNavigation();
        this.pathDist = pathDist;
        this.attackDist = attackDist;
        this.snapDist = snapDist;
        this.teleportToLeaves = teleportToLeaves;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(mob.getNavigation() instanceof GroundPathNavigation) && !(mob.getNavigation() instanceof FlyingPathNavigation) && !(mob.getNavigation() instanceof WaterBoundPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canUse() {
        if (!FamiliarHelper.isFamiliar(mob)) {
            mob.remove(Entity.RemovalReason.DISCARDED);
            return false;
        }

        if (FamiliarHelper.isSitting(mob)) {
            return false;
        }

        Player player = FamiliarHelper.getCaster(mob);
        if (player == null) {
            return false;
        }

        if (mob.distanceToSqr(player) < (double)(snapDist * snapDist)) {
            return false;
        }

        if (mob.getTarget() != null && mob.distanceToSqr(player) < (double)(attackDist * attackDist)) {
            return false;
        }

        owner = player;
        mob.setTarget(null);
        return true;
    }

    public boolean canContinueToUse() {
        return !navigator.isDone() && owner != null;
    }

    public void start() {
        timeToRecalcPath = 0;
        oldWaterCost = mob.getPathfindingMalus(BlockPathTypes.WATER);
        mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public void stop() {
        owner = null;
        navigator.stop();
        mob.setPathfindingMalus(BlockPathTypes.WATER, oldWaterCost);
    }

    public void tick() {
        mob.getLookControl().setLookAt(owner, 10.0F, (float)mob.getMaxHeadXRot());
        if (--timeToRecalcPath <= 0) {
            timeToRecalcPath = 10;
            if (!mob.isLeashed() && !mob.isPassenger()) {
                if (mob.distanceToSqr(owner) >= (double)(pathDist * pathDist)) {
                    tryToTeleportNearEntity();
                } else {
                    navigator.moveTo(owner, followSpeed);
                }
            }
        }
    }

    private void tryToTeleportNearEntity() {
        BlockPos blockpos = owner.blockPosition();

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
        if (Math.abs((double)x - owner.getX()) < 2.0 && Math.abs((double)z - owner.getZ()) < 2.0) {
            return false;
        }
        if (!isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
            return false;
        }

        mob.moveTo((double)x + 0.5, (double)y, (double)z + 0.5, mob.getYRot(), mob.getXRot());
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
                BlockPos blockpos = pos.subtract(this.mob.blockPosition());
                return level.noCollision(mob, mob.getBoundingBox().move(blockpos));
            }
        }
    }

    private int getRandomNumber(int min, int max) {
        return mob.getRandom().nextInt(max - min + 1) + min;
    }
}
