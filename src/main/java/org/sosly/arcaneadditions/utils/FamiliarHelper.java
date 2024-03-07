package org.sosly.arcaneadditions.utils;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.sosly.arcaneadditions.entities.ai.FollowOwnerGoal;

import java.util.UUID;

public class FamiliarHelper {
    public static final String CASTER = "arcaneadditions:caster";
    public static final String FAMILIAR = "arcaneadditions:familiar";
    public static final String LAST_INTERACT = "arcaneadditions:familiar-interact";
    public static final String SITTING = "arcaneadditions:familiar-sitting";

    public static Mob getFamiliar(Player player) {
        if (!hasFamiliar(player)) {
            return null;
        }

        UUID familiarID = player.getPersistentData().getUUID(FAMILIAR);
        if (familiarID == null) {
            return null;
        }

        MinecraftServer server = player.getServer();
        if (server == null) {
            return null;
        }

        Level level = player.level();
        if (level == null) {
            return null;
        }

        ResourceKey<Level> dimension = level.dimension();
        ServerLevel slevel = server.getLevel(dimension);
        if (slevel == null) {
            return null;
        }

        return (Mob) slevel.getEntity(familiarID);
    }

    public static Player getCaster(Mob familiar) {
        if (!isFamiliar(familiar)) {
            return null;
        }

        UUID casterID = familiar.getPersistentData().getUUID(CASTER);
        if (casterID == null) {
            return null;
        }

        MinecraftServer server = familiar.getServer();
        if (server == null) {
            return null;
        }

        Level level = familiar.level();
        if (level == null) {
            return null;
        }

        ResourceKey<Level> dimension = level.dimension();
        ServerLevel slevel = server.getLevel(dimension);
        if (slevel == null) {
            return null;
        }

        return slevel.getPlayerByUUID(casterID);
    }

    public static boolean hasFamiliar(Player player) {
        return player.getPersistentData().hasUUID(FAMILIAR);
    }

    public static boolean isCaster(Entity entity, Player player) {
        return entity.getPersistentData().getUUID(CASTER).equals(player.getUUID());
    }

    public static boolean isFamiliar(Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return false;
        }

        return mob.getPersistentData().hasUUID(CASTER);
    }

    public static boolean isSitting(Mob familiar) {
        return familiar.getPersistentData().getBoolean(SITTING);
    }

    public static void orderFamiliarToSit(Mob familiar) {
        long interact = familiar.getPersistentData().getLong(LAST_INTERACT);
        if (interact > familiar.level().getGameTime() - 20L) {
            return;
        }

        boolean goal = !familiar.getPersistentData().getBoolean(SITTING);
        if (goal) {
            familiar.setTarget(null);
            familiar.getNavigation().stop();
        }

        // todo: add more familiar types?  Maybe there's something less hacky we can do with mixins or something
        if (familiar instanceof TamableAnimal animal) {
            animal.setOrderedToSit(goal);
        } else if (familiar instanceof Fox fox) {
            fox.setSitting(goal);
        }

        familiar.getPersistentData().putLong(LAST_INTERACT, familiar.level().getGameTime());
        familiar.getPersistentData().putBoolean(SITTING, goal);
    }

    public static void setFamiliar(PathfinderMob familiar) {
        // Familiar AI
        familiar.goalSelector.getAvailableGoals().clear();
        familiar.goalSelector.addGoal(0, new FollowOwnerGoal(familiar, 1.0D, 10.0F, 2.0F, 5.0F, true));
        familiar.goalSelector.addGoal(1, new FloatGoal(familiar));
        familiar.goalSelector.addGoal(1, new PanicGoal(familiar, 1.5D));
        familiar.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(familiar, 0.8D, 1.0000001E-5F));
        familiar.goalSelector.addGoal(12, new LookAtPlayerGoal(familiar, Player.class, 10.0F));
    }
}
