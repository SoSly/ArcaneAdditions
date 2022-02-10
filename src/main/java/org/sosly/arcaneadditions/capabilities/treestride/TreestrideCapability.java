/*
 */

package org.sosly.arcaneadditions.capabilities.treestride;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.*;


public class TreestrideCapability implements ITreestrideCapability {
    final Map<BlockPos, Set<UUID>> blocksToPlayers = new HashMap<>();
    final Map<UUID, Map<BlockPos, String>> playersToBlocks = new HashMap<>();
    final Map<ServerPlayer, BlockPos> playerPositions = new HashMap<>();

    @Override
    public void addDestination(Player player, String name, BlockPos pos) {
        UUID uuid = player.getUUID();
        this.addDestination(uuid, name, pos);
    }

    @Override
    public void addDestination(UUID uuid, String name, BlockPos pos) {
        synchronizeDataMaps(uuid, pos);

        Set<UUID> blockData = getBlocksToPlayers(pos);
        Map<BlockPos, String> playerData = getPlayersToBlocks(uuid);

        blockData.add(uuid);
        blocksToPlayers.put(pos, blockData);
        playerData.put(pos, name);
        playersToBlocks.put(uuid, playerData);
    }

    @Override
    public void clearCurrentPosition(ServerPlayer player) {
        playerPositions.remove(player);
    }

    @Override
    public void removeDestination(BlockPos pos) {
        Set<UUID> blockData = getBlocksToPlayers(pos);

        for (UUID uuid : blockData) {
            Map<BlockPos, String> playerData = getPlayersToBlocks(uuid);
            if (playerData.isEmpty()) continue;
            playerData.remove(pos);
        }

        blocksToPlayers.remove(pos);
    }

    @Override
    public void removeDestination(ServerPlayer player, BlockPos pos) {
        UUID uuid = player.getUUID();
        Set<UUID> blockData = getBlocksToPlayers(pos);
        blockData.remove(uuid);
        Map<BlockPos, String> playerData = getPlayersToBlocks(uuid);
        playerData.remove(pos);
    }

    @Override
    public void reset() {
        blocksToPlayers.clear();
        playersToBlocks.clear();
    }

    @Override
    public void setCurrentPosition(ServerPlayer player, BlockPos pos) {
        playerPositions.put(player, pos);
    }

    @Override
    public Map<BlockPos, String> getPlayerDestinations(Player player) {
        return playersToBlocks.get(player.getUUID());
    }

    @Override
    public Map<UUID, Map<BlockPos, String>> getAllDestinations() {
        return playersToBlocks;
    }

    @Override
    @Nullable
    public BlockPos getCurrentPosition(ServerPlayer player) {
        return playerPositions.getOrDefault(player, null);
    }

    private Set<UUID> getBlocksToPlayers(BlockPos pos) {
        return blocksToPlayers.getOrDefault(pos, new HashSet<>());
    }

    private Map<BlockPos, String> getPlayersToBlocks(UUID uuid) {
        return playersToBlocks.getOrDefault(uuid, new HashMap<>());
    }

    /**
     * Validates that both blocksToPlayers and playersToBlocks agree on a Player:BlockPos relationship.
     *
     * If either map thinks the data is present and the other does not, then the data is cleared from the map
     * with the bad information to ensure synchronization.
     */
    private void synchronizeDataMaps(UUID uuid, BlockPos pos) {
        Set<UUID> blockData = getBlocksToPlayers(pos);
        Map<BlockPos, String> playerData = getPlayersToBlocks(uuid);
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
