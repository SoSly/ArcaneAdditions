package org.sosly.arcaneadditions.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.sosly.arcaneadditions.ArcaneAdditions;

import javax.annotation.Nullable;

public class World {
    @Nullable
    public static Entity getLevelEntity(Entity entity) {
        Level level;
        if ((level = ArcaneAdditions.instance.proxy.getClientWorld()) != null) {
            return level.getEntity(entity.getId());
        }
        return null;
    }
}
