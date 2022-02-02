package org.sosly.advancedarcana.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class World {
    @Nullable
    public static Entity getLevelEntity(Entity entity) {
        Minecraft instance = Minecraft.getInstance();
        if (instance != null) {
            Level level = instance.level;
            if (level != null) {
                return level.getEntity(entity.getId());
            }
        }

        return null;
    }

    public static boolean isClientSide() {
        Minecraft instance = Minecraft.getInstance();
        if (instance != null) {
            Level level = instance.level;
            if (level != null) {
                return level.isClientSide;
            }
        }

        return true; // todo: This is technically not provable, since we don't have a way to check.
    }
}
