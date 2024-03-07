package org.sosly.arcaneadditions.capabilities.familiar;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class FamiliarCapability implements IFamiliarCapability {
    private UUID caster;
    private WeakReference<Player> casterPlayer;
    private int familiar;
    private WeakReference<TamableAnimal> familiarAnimal;

    @Override
    public WeakReference<Player> getCaster(Level level) {
        if (casterPlayer != null) return this.casterPlayer;
        if (caster != null && level.getPlayerByUUID(caster) != null) {
            this.casterPlayer = new WeakReference<>(level.getPlayerByUUID(caster));
            return this.casterPlayer;
        }
        return null;
    }

    @Override
    public UUID getCasterUUID() {
        return this.caster;
    }

    @Override
    public void setCaster(Player player) {
        this.caster = player.getUUID();
        this.casterPlayer = new WeakReference<>(player);
    }

    @Override
    public void setCasterUUID(UUID caster) {
        this.caster = caster;
    }

    @Override
    public WeakReference<TamableAnimal> getFamiliar(Level level) {
        if (familiarAnimal != null) return this.familiarAnimal;
        if (familiar != 0) {
            TamableAnimal animal = (TamableAnimal) level.getEntity(familiar);
            if (animal != null) {
                this.familiarAnimal = new WeakReference<>(animal);
                return this.familiarAnimal;
            }
        }
        return null;
    }

    @Override
    public boolean hasFamiliar() {
        return familiar != 0;
    }

    @Override
    public int getFamiliarID() {
        return familiar;
    }

    @Override
    public void setFamiliar(TamableAnimal familiar) {
        this.familiarAnimal = new WeakReference<>(familiar);
        this.familiar = familiar.getId();
    }

    @Override
    public void setFamiliarID(int familiar) {
        this.familiar = familiar;
    }

    @Override
    public void remove() {
        this.caster = null;
        this.casterPlayer = null;
        this.familiarAnimal = null;
        this.familiar = 0;
    }
}
