/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.capabilities.polymorph;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class PolymorphCapability implements IPolymorphCapability {
    private UUID caster;
    private WeakReference<Player> casterPlayer;
    private float complexity = 0.0f;
    private float health = 0.0f;
    private boolean morphing = false;

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
    public float getComplexity() {
        return complexity;
    }

    @Override
    public float getHealth() {
        return health;
    }

    @Override
    public boolean isMorphing() { return morphing; }

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
    public void setComplexity(float complexity) {
        this.complexity = complexity;
    }

    @Override
    public void setMorphing(boolean value) { this.morphing = value; }

    @Override
    public void setHealth(float health) {
        this.health = health;
    }

    @Override
    public void reset() {
        this.caster = null;
        this.casterPlayer = null;
        this.complexity = 0.0f;
        this.health = 0.0f;
    }
}
