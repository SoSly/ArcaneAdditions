package org.sosly.arcaneadditions.capabilities.polymorph;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.sosly.arcaneadditions.utils.RLoc;

import java.lang.ref.WeakReference;
import java.util.UUID;

public interface IPolymorphCapability {
    ResourceLocation POLYMORPH_CAPABILITY = RLoc.create("polymorph");

    WeakReference<Player> getCaster(Level value);
    UUID getCasterUUID();
    float getComplexity();
    float getHealth();
    void setCaster(Player value);
    void setCasterUUID(UUID value);
    void setComplexity(float value);
    void setHealth(float value);
    void reset();
}
