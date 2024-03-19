package org.sosly.arcaneadditions.utils;

import com.mna.api.entities.ai.CastSpellAtTargetGoal;
import com.mna.api.entities.ai.CastSpellOnSelfGoal;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.entities.ai.*;
import org.sosly.arcaneadditions.entities.ai.FollowOwnerGoal;

import java.util.UUID;

public class FamiliarHelper {
    public static final String CASTER = "arcaneadditions:caster";
    public static final String FAMILIAR = "arcaneadditions:familiar";
    public static final String LAST_INTERACT = "arcaneadditions:familiar/last_interact";
    public static final String ORDERED_TO_STAY = "arcaneadditions:familiar/ordered_to_stay";

    public static Mob getFamiliar(Player player) {
        if (!hasFamiliar(player)) {
            return null;
        }

        UUID familiarID = player.getPersistentData().getUUID(FAMILIAR);
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

    public static boolean isOrderedToStay(Mob familiar) {
        return familiar.getPersistentData().getBoolean(ORDERED_TO_STAY);
    }

    public static void setInStayingPose(Mob familiar, boolean shouldStay) {
        long interact = familiar.getPersistentData().getLong(LAST_INTERACT);
        if (interact > familiar.level().getGameTime() - 20L) {
            return;
        }

        // todo: add more familiar types?  Maybe there's something less hacky we can do with mixins or something
        if (familiar instanceof TamableAnimal animal) {
            if (animal instanceof Parrot parrot && parrot.canSitOnShoulder()) {
                parrot.setEntityOnShoulder((ServerPlayer) FamiliarHelper.getCaster(familiar));
            }
            animal.setOrderedToSit(shouldStay);
        } else if (familiar instanceof Fox fox) {
            fox.setSitting(shouldStay);
        } else if (familiar instanceof Bat bat) {
            bat.setResting(shouldStay);
        }

        familiar.getPersistentData().putLong(LAST_INTERACT, familiar.level().getGameTime());
        familiar.getPersistentData().putBoolean(ORDERED_TO_STAY, shouldStay);
    }

    public static void addFamiliarAI(Mob familiar, Player owner) {
        ArcaneAdditions.LOGGER.info("here");

        // Remove goals that we don't want
        familiar.goalSelector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof AvoidEntityGoal);
        familiar.goalSelector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof BreedGoal);
        familiar.goalSelector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof CastSpellAtTargetGoal);
        familiar.goalSelector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof CastSpellOnSelfGoal);
        familiar.goalSelector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof EatBlockGoal);
        familiar.goalSelector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof PanicGoal);
        familiar.goalSelector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof PathfindToRaidGoal);
        familiar.goalSelector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof TemptGoal);
        familiar.targetSelector.removeAllGoals((g) -> true);

        // Add new goals
        familiar.goalSelector.addGoal(2, new StayWhenOrderedToGoal(familiar));
        familiar.goalSelector.addGoal(6, new FollowOwnerGoal(familiar, 1.0D, 10.0F, 2.0F, 5.0F, true));
        familiar.targetSelector.addGoal(0, new NeverTargetOwnerGoal(familiar));
        familiar.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(familiar));
        familiar.targetSelector.addGoal(2, new OwnerHurtTargetGoal(familiar));

        ArcaneAdditions.LOGGER.info("here");
        // todo: add a defend owner (see: OwnerHurtByTargetGoal and TargetDefendOwnerGoal) p1
        // todo: add goals for spellcasting p1
    }
}
