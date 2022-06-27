/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.entities.sorcery;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class SoulSearchersBeamEntity extends Entity {
    private static final EntityDataAccessor<Integer> SOURCE_ID = SynchedEntityData.defineId(SoulSearchersBeamEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(SoulSearchersBeamEntity.class, EntityDataSerializers.INT);
    private static final String NBT_SOURCE_ID = "source_id";
    private static final String NBT_TARGET_ID = "target_id";
    public SoulSearchersBeamEntity(EntityType<? extends SoulSearchersBeamEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.setInvulnerable(true);
    }

    public LivingEntity getSource(Level level) {
        int entityID = this.entityData.get(SOURCE_ID);
        Entity entity = level.getEntity(entityID);
        if (entity instanceof LivingEntity livingEntity) {
            return livingEntity;
        }
        return null;
    }

    public LivingEntity getTarget(Level level) {
        int entityID = this.entityData.get(TARGET_ID);
        Entity entity = level.getEntity(entityID);
        if (entity instanceof LivingEntity livingEntity) {
            return livingEntity;
        }
        return null;
    }

    public void setSource(LivingEntity source) {
        if (source != null) {
            this.entityData.set(SOURCE_ID, source.getId());
            this.setPos(source.getX(), source.getY(), source.getZ());
            this.setRot(source.getYRot(), source.getXRot());
        }
    }

    public void setTarget(LivingEntity target) {
        if (target != null) {
            this.entityData.set(TARGET_ID, target.getId());
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SOURCE_ID, -1);
        this.entityData.define(TARGET_ID, -1);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains(NBT_SOURCE_ID)) {
            this.entityData.set(SOURCE_ID, tag.getInt(NBT_SOURCE_ID));
        }

        if (tag.contains(NBT_TARGET_ID)) {
            this.entityData.set(TARGET_ID, tag.getInt(NBT_TARGET_ID));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        int sourceId = this.entityData.get(SOURCE_ID);
        int targetId = this.entityData.get(TARGET_ID);
        tag.putInt(NBT_SOURCE_ID, sourceId);
        tag.putInt(NBT_TARGET_ID, targetId);
    }

    @Override
    @NotNull
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
