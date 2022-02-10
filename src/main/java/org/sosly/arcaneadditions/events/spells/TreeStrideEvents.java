/*
 */

package org.sosly.arcaneadditions.events.spells;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.capabilities.treestride.TreestrideProvider;
import org.sosly.arcaneadditions.spells.components.TreeStrideComponent;
import org.sosly.arcaneadditions.utils.TreeFinder;

import java.util.Set;

@Mod.EventBusSubscriber(modid = ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TreeStrideEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockState blockState = event.getState();
        BlockPos pos = event.getPos();

        if (!TreeFinder.isBlockALog(blockState)
                || event.isCanceled()
                || !(event.getWorld() instanceof ServerLevel level)
                || !(event.getPlayer() instanceof ServerPlayer agent)
                || !blockState.canHarvestBlock(level, pos, agent)
        ) { return; }

        if (!TreeFinder.isPartOfATree(level, pos, true)) return;
        Set<BlockPos> roots = TreeFinder.getRootBlocks(level, pos,  blockPos -> TreeFinder.isBlockALog(level, blockPos));
        BlockPos root = TreeStrideComponent.findRootBlock(roots, pos);
        level.getCapability(TreestrideProvider.TREESTRIDE).ifPresent(treestride -> {
            treestride.removeDestination(root);
        });
    }
}
