package org.sosly.arcaneadditions.capabilities.familiar;

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
        if (instance.getFamiliarID() != 0) {
            nbt.putInt("familiar", instance.getFamiliarID());
        }
        if (instance.getCasterUUID() != null) {
            nbt.putUUID("caster", instance.getCasterUUID());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        IFamiliarCapability instance = this.holder.orElse(new FamiliarCapability());
        if (nbt instanceof CompoundTag cnbt) {
            if (cnbt.contains("familiar")) {
                instance.setFamiliarID(cnbt.getInt("familiar"));
            }
            if (cnbt.contains("caster")) {
                instance.setCasterUUID(cnbt.getUUID("caster"));
            }
        }
    }
}
