/*
 */

package org.sosly.arcaneadditions.capabilities.treestride;

import net.minecraft.core.BlockPos;
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

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TreestrideProvider implements ICapabilitySerializable<Tag> {
    public static final Capability<ITreestrideCapability> TREESTRIDE = CapabilityManager.get(new CapabilityToken<>() {});
    private final LazyOptional<ITreestrideCapability> holder = LazyOptional.of(TreestrideCapability::new);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return TREESTRIDE.orEmpty(cap, this.holder);
    }

    @Override
    public Tag serializeNBT() {
        ITreestrideCapability instance = this.holder.orElse(new TreestrideCapability());
        return TreestrideProvider.serializeNBT(instance);
    }

    public static Tag serializeNBT(ITreestrideCapability instance) {
        CompoundTag nbt = new CompoundTag();

        AtomicInteger playerCount = new AtomicInteger(0);
        instance.getAllDestinations().forEach((uuid, playerData) -> {
            CompoundTag playerNBT = new CompoundTag();
            playerNBT.putUUID("uuid", uuid);
            AtomicInteger destinationCount = new AtomicInteger(0);
            playerData.forEach((pos, name) -> {
                CompoundTag destNBT = new CompoundTag();
                destNBT.putInt("x", pos.getX());
                destNBT.putInt("y", pos.getY());
                destNBT.putInt("z", pos.getZ());
                destNBT.putString("name", name);
                int index = destinationCount.getAndIncrement();
                playerNBT.put(Integer.toString(index), destNBT);
            });
            playerNBT.putInt("destinations", destinationCount.get());
            int index = playerCount.getAndIncrement();
            nbt.put(Integer.toString(index), playerNBT);
        });
        nbt.putInt("players", playerCount.get());
        return nbt;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        ITreestrideCapability instance = this.holder.orElse(new TreestrideCapability());
        if (nbt instanceof CompoundTag cnbt) {
            int playerCount = cnbt.getInt("players");
            do {
                if (cnbt.get(Integer.toString(playerCount)) instanceof CompoundTag playerNBT) {
                    UUID uuid = playerNBT.getUUID("uuid");
                    int destinationCount = playerNBT.getInt("destinations");
                    do {
                        if (playerNBT.get(Integer.toString(destinationCount)) instanceof CompoundTag destNBT) {
                            BlockPos dest = new BlockPos(destNBT.getInt("x"), destNBT.getInt("y"), destNBT.getInt("z"));
                            String name = destNBT.getString("name");
                            instance.addDestination(uuid, name, dest);
                        }
                    } while (destinationCount-- > 0);
                }
            } while (playerCount-- > 0);
        }
    }
}
