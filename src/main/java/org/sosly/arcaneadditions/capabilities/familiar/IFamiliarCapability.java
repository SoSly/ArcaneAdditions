package org.sosly.arcaneadditions.capabilities.familiar;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.sosly.arcaneadditions.utils.RLoc;

import java.lang.ref.WeakReference;
import java.util.UUID;

public interface IFamiliarCapability {
    ResourceLocation FAMILIAR_CAPABILITY = RLoc.create("familiar");

    WeakReference<Player> getCaster(Level value);
    UUID getCasterUUID();
    void setCaster(Player value);
    void setCasterUUID(UUID value);

    WeakReference<TamableAnimal> getFamiliar(Level value);
    boolean hasFamiliar();
    int getFamiliarID();
    void setFamiliar(TamableAnimal value);
    void setFamiliarID(int value);
    void remove();
}
