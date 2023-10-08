/*
 *   Arcane Additions Copyright (c) 2023, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.entities.sorcery;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.sosly.arcaneadditions.effects.EffectRegistry;
import org.sosly.arcaneadditions.entities.EntityRegistry;

import java.util.Optional;
import java.util.UUID;

public class AstralProjectionEntity extends Mob {
    private static final EntityDataAccessor<Optional<UUID>> RENDER_AS_UUID;
    private Player cachedController;

    public AstralProjectionEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }

    public AstralProjectionEntity(Player controller, Level level) {
        this(EntityRegistry.ASTRAL_PROJECTION.get(), level);
        this.setPlayer(controller);
    }

    public Player getPlayer() {
        if (this.cachedController != null) {
            return this.cachedController;
        }

        Optional<UUID> param = this.entityData.get(RENDER_AS_UUID);
        if (!param.isPresent()) {
            return null;
        }

        UUID uuid = param.get();
        this.cachedController = this.level.getPlayerByUUID(uuid);
        return this.cachedController;
    }

    public void setPlayer(Player controller) {
        this.entityData.set(RENDER_AS_UUID, Optional.of(controller.getUUID()));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RENDER_AS_UUID, Optional.empty());
    }

    public static AttributeSupplier.Builder getGlobalAttributes() {
        return Mob.createMobAttributes();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level.isClientSide()) {
            return;
        }

        Player controller = this.getPlayer();
        if (controller == null) {
            this.remove(Entity.RemovalReason.DISCARDED);
            return;
        }

        if (!controller.hasEffect(EffectRegistry.ASTRAL_PROJECTION.get()) || !this.hasEffect(EffectRegistry.ASTRAL_PROJECTION.get())) {
            this.remove(Entity.RemovalReason.DISCARDED);
            return;
        }

        if (!controller.isAlive()) {
            this.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    static {
        RENDER_AS_UUID = SynchedEntityData.defineId(AstralProjectionEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    }
}
