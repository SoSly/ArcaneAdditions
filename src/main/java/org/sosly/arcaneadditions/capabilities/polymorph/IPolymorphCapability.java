package org.sosly.arcaneadditions.capabilities.polymorph;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.sosly.arcaneadditions.utils.RLoc;

import java.lang.ref.WeakReference;
import java.util.UUID;

public interface IPolymorphCapability {
    public static final ResourceLocation POLYMORPH_CAPABILITY = RLoc.create("polymorph");

    public WeakReference<Player> getCaster(Level value);
    public UUID getCasterUUID();
    public float getComplexity();
    public float getHealth();
    public void setCaster(Player value);
    public void setCasterUUID(UUID value);
    public void setComplexity(float value);
    public void setHealth(float value);

    public void reset();
}
