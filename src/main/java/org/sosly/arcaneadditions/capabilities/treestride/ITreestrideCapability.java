/*
 */

package org.sosly.arcaneadditions.capabilities.treestride;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.sosly.arcaneadditions.utils.RLoc;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public interface ITreestrideCapability {
    ResourceLocation TREESTRIDE_CAPABILITY = RLoc.create("treestride");

    void addDestination(Player player, String name, BlockPos pos);
    void addDestination(UUID uuid, String name, BlockPos pos);
    void clearCurrentPosition(ServerPlayer player);
    Map<BlockPos, String> getPlayerDestinations(Player player);
    Map<UUID, Map<BlockPos, String>> getAllDestinations();
    @Nullable BlockPos getCurrentPosition(ServerPlayer player);
    void removeDestination(BlockPos pos);
    void removeDestination(ServerPlayer player, BlockPos pos);
    void reset();
    void setCurrentPosition(ServerPlayer player, BlockPos pos);
}
