package org.sosly.arcaneadditions.capabilities.familiar;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FamiliarProvider implements ICapabilitySerializable<Tag> {
    public static final Capability<IFamiliarCapability> FAMILIAR = CapabilityManager.get(new CapabilityToken<>() {});
    private final LazyOptional<IFamiliarCapability> holder = LazyOptional.of(FamiliarCapability::new);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return FAMILIAR.orEmpty(cap, this.holder);
    }

    @Override
    public Tag serializeNBT() {
        IFamiliarCapability instance = this.holder.orElse(new FamiliarCapability());
        CompoundTag nbt = new CompoundTag();
        if (instance.getCaster() != null) {
            nbt.putUUID("caster", instance.getCaster().getUUID());
        }
        if (instance.isBapped()) {
            nbt.putBoolean("bapped", instance.isBapped());
        }
        if (instance.getCastingResource() != null) {
            nbt.putString("castingResourceId", instance.getCastingResource().getRegistryName().toString());
            instance.getCastingResource().writeNBT(nbt);
        }
        if (instance.getFamiliar() != null) {
            nbt.putUUID("familiar", instance.getFamiliar().getUUID());
        }
        if (instance.getLastInteract() > 0) {
            nbt.putLong("lastInteract", instance.getLastInteract());
        }
        if (!instance.getName().isEmpty()) {
            nbt.putString("name", instance.getName());
        }
        if (instance.getType() != null) {
            nbt.putString("type", ForgeRegistries.ENTITY_TYPES.getResourceKey(instance.getType()).get().location().toString());
        }
        if (instance.isOrderedToStay()) {
            nbt.putBoolean("orderedToStay", instance.isOrderedToStay());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        IFamiliarCapability instance = this.holder.orElse(new FamiliarCapability());
        if (nbt instanceof CompoundTag cnbt) {
            if (cnbt.contains("bapped")) {
                instance.setBapped(cnbt.getBoolean("bapped"));
            }
            if (cnbt.contains("castingResourceId")) {
                instance.setCastingResourceType(new ResourceLocation(cnbt.getString("castingResourceId")));
            }
            instance.getCastingResource().readNBT(cnbt);
            instance.getCastingResource().setNeedsSync();
            if (instance.getFamiliar() != null) {
                instance.setFamiliarUUID(cnbt.getUUID("familiar"));
            }
            if (cnbt.contains("lastInteract")) {
                instance.setLastInteract(cnbt.getLong("lastInteract"));
            }
            if (cnbt.contains("orderedToStay")) {
                instance.setOrderedToStay(cnbt.getBoolean("orderedToStay"));
            }
            if (cnbt.contains("name")) {
                instance.setName(cnbt.getString("name"));
            }
            if (cnbt.contains("type")) {
                EntityType<?> type = EntityType.byString(cnbt.getString("type")).orElse(null);
                if (type == null) {
                    throw new RuntimeException("Could not get type for Familiar.");
                }
                instance.setType((EntityType<? extends Mob>) type);
            }
        }
    }
}
