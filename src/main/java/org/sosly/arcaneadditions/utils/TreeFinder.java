/*
 */
package org.sosly.arcaneadditions.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.sosly.arcaneadditions.ArcaneAdditions;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Detecting trees is hard. Thankfully, HT has already done this in TreeChop so we're going to borrow his algorithm
// and credit him instead of developing our own algorithm for this.
@Mod.EventBusSubscriber(modid = ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TreeFinder {
    public static Iterable<Holder<Block>> logs;
    public static Iterable<Holder<Block>> leaves;

    private static int maxNumTreeBlocks = 64; // todo make this a configuration value

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        TagKey<Block> leavesTag = BlockTags.create(new ResourceLocation("arcaneadditions:leaves"));
        TagKey<Block> trunksTag = BlockTags.create(new ResourceLocation("arcaneadditions:trunks"));

        leaves = event.getRegistryAccess().registryOrThrow(Registry.BLOCK_REGISTRY).getTagOrEmpty(leavesTag);
        logs = event.getRegistryAccess().registryOrThrow(Registry.BLOCK_REGISTRY).getTagOrEmpty(trunksTag);
    }

    public static Set<BlockPos> getConnectedBlocks(Collection<BlockPos> startingPoints, Function<BlockPos, Stream<BlockPos>> searchOffsetsSupplier, int maxNumBlocks, AtomicInteger iterationCounter) {
        Set<BlockPos> connectedBlocks = new HashSet<>();
        List<BlockPos> newConnectedBlocks = new LinkedList<>(startingPoints);
        iterationCounter.set(0);
        do {
            connectedBlocks.addAll(newConnectedBlocks);
            if (connectedBlocks.size() >= maxNumBlocks) {
                break;
            }

            newConnectedBlocks = newConnectedBlocks.stream()
                    .flatMap(blockPos -> searchOffsetsSupplier.apply(blockPos)
                            .filter(pos1 -> !connectedBlocks.contains(pos1))
                    )
                    .limit(maxNumBlocks - connectedBlocks.size())
                    .collect(Collectors.toList());

            iterationCounter.incrementAndGet();
        } while (!newConnectedBlocks.isEmpty());

        return connectedBlocks;
    }

    public static Set<BlockPos> getConnectedBlocks(Collection<BlockPos> startingPoints, Function<BlockPos, Stream<BlockPos>> searchOffsetsSupplier, int maxNumBlocks) {
        return getConnectedBlocks(startingPoints, searchOffsetsSupplier, maxNumBlocks, new AtomicInteger());
    }

    public static Set<BlockPos> getTreeBlocks(Level level, BlockPos blockPos, Predicate<BlockPos> logCondition, AtomicBoolean inHasLeaves) {
        if (!logCondition.test(blockPos)) {
            return Collections.emptySet();
        }

        AtomicBoolean overrideHasLeaves = new AtomicBoolean(inHasLeaves.get());
        boolean valueToOverrideHasLeaves = inHasLeaves.get();

        AtomicBoolean trueHasLeaves = new AtomicBoolean(false);
        Set<BlockPos> supportedBlocks = getConnectedBlocks(
                Collections.singletonList(blockPos),
                somePos -> BlockNeighbors.HORIZONTAL_AND_ABOVE.asStream(somePos)
                        .peek(pos -> trueHasLeaves.compareAndSet(false, isBlockLeaves(level, pos)))
                        .filter(logCondition),
                maxNumTreeBlocks
        );

        if (supportedBlocks.size() >= maxNumTreeBlocks) {
            // todo: log
        }

        inHasLeaves.set(overrideHasLeaves.get() ? valueToOverrideHasLeaves : trueHasLeaves.get());

        return supportedBlocks;
    }

    public static Set<BlockPos> getRootBlocks(Level level, BlockPos blockPos, Predicate<BlockPos> logCondition) {
        if (!logCondition.test(blockPos)) {
            return Collections.emptySet();
        }

        Set<BlockPos> supportingBlocks = getConnectedBlocks(
                Collections.singletonList(blockPos),
                somePos -> BlockNeighbors.BELOW.asStream(somePos).filter(logCondition),
                maxNumTreeBlocks
        );

        return supportingBlocks;
    }

    public static boolean isBlockALog(BlockState blockState) {
        AtomicBoolean isLog = new AtomicBoolean(false);
        logs.forEach(log -> {
            ResourceLocation loc = ForgeRegistries.BLOCKS.getKey(blockState.getBlock());

            if (loc != null && log.is(loc)) {
                isLog.set(true);
            }
        });
        return isLog.get();
    }

    public static boolean isBlockALog(Level level, BlockPos pos) {
        return isBlockALog(level.getBlockState(pos));
    }

    public static boolean isBlockLeaves(Level level, BlockPos pos) {
        return isBlockLeaves(level.getBlockState(pos));
    }

    public static boolean isBlockLeaves(BlockState blockState) {
        AtomicBoolean isLeaves = new AtomicBoolean(false);

        leaves.forEach(leaf -> {
            ResourceLocation loc = ForgeRegistries.BLOCKS.getKey(blockState.getBlock());

            if (loc != null && leaf.is(loc)) {
                isLeaves.set(true);
            }
        });

        if (isLeaves.get()) {
            return !blockState.hasProperty(LeavesBlock.PERSISTENT) || !blockState.getValue(LeavesBlock.PERSISTENT);
        } else {
            return false;
        }
    }

    public static boolean isPartOfATree(Level level, BlockPos pos, boolean mustHaveLeaves) {
        AtomicBoolean hasLeaves = new AtomicBoolean(false);
        Set<BlockPos> treeBlocks = getTreeBlocks(level, pos, blockPos -> isBlockALog(level, blockPos), hasLeaves);

        if (treeBlocks.isEmpty()) {
            return false;
        } else {
            if (mustHaveLeaves) {
                return hasLeaves.get();
            } else {
                return treeBlocks.size() >= (hasLeaves.get() ? 1 : 2);
            }
        }
    }

    public static class BlockNeighbors {
        protected final BlockPos[] blocks;

        static public final BlockNeighbors HORIZONTAL_ADJACENTS = new BlockNeighbors(Stream.of(
                new BlockPos(-1, 0, 0),
                new BlockPos(0, 0, -1),
                new BlockPos(1, 0, 0),
                new BlockPos(0, 0, 1)
        ).toArray(BlockPos[]::new));

        static public final BlockNeighbors VERTICAL_ADJACENTS = new BlockNeighbors(Stream.of(
                new BlockPos(0, -1, 0),
                new BlockPos(0, 1, 0)
        ).toArray(BlockPos[]::new));

        static public final BlockNeighbors ADJACENTS = new BlockNeighbors(Stream.of(
                HORIZONTAL_ADJACENTS.asStream(),
                VERTICAL_ADJACENTS.asStream()
        ).flatMap(a -> a).toArray(BlockPos[]::new));

        static public final BlockNeighbors HORIZONTAL_DIAGONALS = new BlockNeighbors(Stream.of(
                new BlockPos(-1, 0, -1),
                new BlockPos(-1, 0, 1),
                new BlockPos(1, 0, -1),
                new BlockPos(1, 0, 1)
        ).toArray(BlockPos[]::new));

        static public final BlockNeighbors HORIZONTAL = new BlockNeighbors(Stream.of(
                HORIZONTAL_ADJACENTS.asStream(),
                HORIZONTAL_DIAGONALS.asStream()
        ).flatMap(a -> a).toArray(BlockPos[]::new));

        static public final BlockNeighbors ABOVE_ADJACENTS = new BlockNeighbors(Stream.of(
                new BlockPos(-1, 1, 0),
                new BlockPos(0, 1, -1),
                new BlockPos(1, 1, 0),
                new BlockPos(0, 1, 1)
        ).toArray(BlockPos[]::new));

        static public final BlockNeighbors ABOVE_DIAGONALS = new BlockNeighbors(Stream.of(
                new BlockPos(-1, 1, -1),
                new BlockPos(-1, 1, 1),
                new BlockPos(1, 1, -1),
                new BlockPos(1, 1, 1)
        ).toArray(BlockPos[]::new));

        static public final BlockNeighbors ABOVE = new BlockNeighbors(Stream.of(
                ABOVE_ADJACENTS.asStream(),
                ABOVE_DIAGONALS.asStream(),
                Stream.of(new BlockPos(0, 1, 0))
        ).flatMap(a -> a).toArray(BlockPos[]::new));

        static public final BlockNeighbors HORIZONTAL_AND_ABOVE = new BlockNeighbors(Stream.of(
                HORIZONTAL.asStream(),
                ABOVE.asStream()
        ).flatMap(a -> a).toArray(BlockPos[]::new));

        static public final BlockNeighbors BELOW_ADJACENTS = new BlockNeighbors(Stream.of(
                new BlockPos(-1, -1, 0),
                new BlockPos(0, -1, -1),
                new BlockPos(1, -1, 0),
                new BlockPos(0, -1, 1)
        ).toArray(BlockPos[]::new));

        static public final BlockNeighbors BELOW_DIAGONALS = new BlockNeighbors(Stream.of(
                new BlockPos(-1, -1, -1),
                new BlockPos(-1, -1, 1),
                new BlockPos(1, -1, -1),
                new BlockPos(1, -1, 1)
        ).toArray(BlockPos[]::new));

        static public final BlockNeighbors BELOW = new BlockNeighbors(Stream.of(
                BELOW_ADJACENTS.asStream(),
                BELOW_DIAGONALS.asStream(),
                Stream.of(new BlockPos(0, -1, 0))
        ).flatMap(a -> a).toArray(BlockPos[]::new));

        static public final BlockNeighbors ADJACENTS_AND_DIAGONALS = new BlockNeighbors(Stream.of(
                ABOVE.asStream(),
                HORIZONTAL.asStream(),
                BELOW.asStream()
        ).flatMap(a -> a).toArray(BlockPos[]::new));

        static public final BlockNeighbors ADJACENTS_AND_BELOW_ADJACENTS = new BlockNeighbors(Stream.of(
                ADJACENTS.asStream(),
                BELOW_ADJACENTS.asStream()
        ).flatMap(a -> a).toArray(BlockPos[]::new));

        public BlockNeighbors(BlockPos[] blocks) {
            this.blocks = blocks;
        }

        protected Stream<BlockPos> asStream() {
            return Arrays.stream(blocks);
        }

        public Stream<BlockPos> asStream(BlockPos pos) {
            return Arrays.stream(blocks).map(pos::offset);
        }
    }
}
