/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/
 */

package org.sosly.arcaneadditions.capabilities.treestride;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.sosly.arcaneadditions.utils.RLoc;

import java.util.Map;
import java.util.UUID;

public interface ITreestrideCapability {
    ResourceLocation TREESTRIDE_CAPABILITY = RLoc.create("treestride");

    void addDestination(ServerPlayer player, String name, BlockPos pos);
    void addDestination(UUID uuid, String name, BlockPos pos);
    void removeDestination(BlockPos pos);
    Map<BlockPos, String> getPlayerDestinations(ServerPlayer player);
    Map<UUID, Map<BlockPos, String>> getAllDestinations();
}
