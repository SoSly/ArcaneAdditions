package org.sosly.arcaneadditions.capabilities.polymorph;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PolymorphProvider implements ICapabilitySerializable<Tag> {
    public static final Capability<IPolymorphCapability> POLYMORPH = CapabilityManager.get(new CapabilityToken<>() {});
    private final LazyOptional<IPolymorphCapability> holder = LazyOptional.of(PolymorphCapability::new);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return POLYMORPH.orEmpty(cap, this.holder);
    }

    @Override
    public Tag serializeNBT() {
        IPolymorphCapability instance = this.holder.orElse(new PolymorphCapability());
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("complexity", instance.getComplexity());
        nbt.putFloat("health", instance.getHealth());
        if (instance.getCasterUUID() != null) {
            nbt.putUUID("caster", instance.getCasterUUID());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        IPolymorphCapability instance = this.holder.orElse(new PolymorphCapability());
        if (nbt instanceof CompoundTag cnbt) {
            if (cnbt.contains("complexity")) {
                instance.setComplexity(cnbt.getFloat("complexity"));
            }
            if (cnbt.contains("health")) {
                instance.setHealth(cnbt.getFloat("health"));
            }
            if (cnbt.contains("caster")) {
                instance.setCasterUUID(cnbt.getUUID("caster"));
            }
        }
    }
}
