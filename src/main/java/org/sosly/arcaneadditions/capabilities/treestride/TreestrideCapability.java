/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/
 */

package org.sosly.arcaneadditions.capabilities.treestride;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class TreestrideCapability implements ITreestrideCapability {
    Map<BlockPos, Set<UUID>> blocksToPlayers = new HashMap<>();
    Map<UUID, Map<BlockPos, String>> playersToBlocks = new HashMap<>();

    @Override
    public void addDestination(ServerPlayer player, String name, BlockPos pos) {
        UUID uuid = player.getUUID();
        this.addDestination(uuid, name, pos);
    }

    @Override
    public void addDestination(UUID uuid, String name, BlockPos pos) {
        synchronizeDataMaps(uuid, pos);

        Set<UUID> blockData = blocksToPlayers.getOrDefault(pos, Set.of());
        Map<BlockPos, String> playerData = playersToBlocks.getOrDefault(uuid, null);

        blockData.add(uuid);
        blocksToPlayers.put(pos, blockData);
        playerData.put(pos, name);
        playersToBlocks.put(uuid, playerData);
    }

    @Override
    public void removeDestination(BlockPos pos) {
        Set<UUID> blockData = blocksToPlayers.getOrDefault(pos, Set.of());

        for (UUID uuid : blockData) {
            Map<BlockPos, String> playerData = playersToBlocks.getOrDefault(uuid, null);
            if (playerData.isEmpty()) continue;
            playerData.remove(pos);
        }

        blocksToPlayers.remove(pos);
    }

    @Override
    public Map<BlockPos, String> getPlayerDestinations(ServerPlayer player) {
        return playersToBlocks.get(player.getUUID());
    }

    @Override
    public Map<UUID, Map<BlockPos, String>> getAllDestinations() {
        return playersToBlocks;
    }

    /**
     * Validates that both blocksToPlayers and playersToBlocks agree on a Player:BlockPos relationship.
     *
     * If either map thinks the data is present and the other does not, then the data is cleared from the map
     * with the bad information to ensure synchronization.
     */
    private void synchronizeDataMaps(UUID uuid, BlockPos pos) {
        Set<UUID> blockData = blocksToPlayers.getOrDefault(pos, Set.of());
        Map<BlockPos, String> playerData = playersToBlocks.getOrDefault(uuid, null);

        if (blockData.contains(uuid) && !playerData.containsKey(pos)) {
            blockData.remove(uuid);
            blocksToPlayers.put(pos, blockData);
        }

        if (!blockData.contains(uuid) && playerData.containsKey(pos)) {
            playerData.remove(pos);
            playersToBlocks.put(uuid, playerData);
        }
    }
}
