package org.sosly.arcaneadditions.capabilities.familiar;

import com.mna.api.capabilities.resource.ICastingResource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.sosly.arcaneadditions.utils.RLoc;

import java.util.UUID;

public interface IFamiliarCapability {
    ResourceLocation FAMILIAR_CAPABILITY = RLoc.create("familiar");

    boolean isBapped();
    void setBapped(boolean value);

    Player getCaster();
    void setCaster(Player value);

    ICastingResource getCastingResource();
    void setCastingResourceType(ResourceLocation var1);

    Mob getFamiliar();
    void setFamiliar(Mob value);
    void setFamiliarUUID(UUID value);

    long getLastInteract();
    void setLastInteract(long value);

    String getName();
    void setName(String value);

    EntityType<? extends Mob> getType();
    void setType(EntityType<? extends Mob> value);


    boolean isOrderedToStay();
    void setOrderedToStay(boolean value);
    void reset();

    void tick();
}
