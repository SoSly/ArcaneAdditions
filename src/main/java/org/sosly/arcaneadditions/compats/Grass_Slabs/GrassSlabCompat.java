/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.compats.Grass_Slabs;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.kuraion.grassslabs.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.sosly.arcaneadditions.compats.ICompat;
import org.sosly.arcaneadditions.spells.components.PathComponent;

import java.util.Map;
import java.util.function.Consumer;

public class GrassSlabCompat implements ICompat, IPathableBlockProxy {
    private static final Lazy<GrassSlabBlock> GRASS_SLAB = Lazy.of(() -> (GrassSlabBlock) ForgeRegistries.BLOCKS.getValue(new ResourceLocation("grassslabs:grass_slab")));
    private static final Lazy<GrassStairsBlock> GRASS_STAIRS = Lazy.of(() -> (GrassStairsBlock) ForgeRegistries.BLOCKS.getValue(new ResourceLocation("grassslabs:grass_stairs")));
    private static final Lazy<GrassCarpetBlock> GRASS_CARPET = Lazy.of(() -> (GrassCarpetBlock) ForgeRegistries.BLOCKS.getValue(new ResourceLocation("grassslabs:grass_carpet")));
    private static final Lazy<DirtSlabBlock> DIRT_SLAB = Lazy.of(() -> (DirtSlabBlock) ForgeRegistries.BLOCKS.getValue(new ResourceLocation("grassslabs:dirt_slab")));
    private static final Lazy<DirtStairsBlock> DIRT_STAIRS = Lazy.of(() -> (DirtStairsBlock) ForgeRegistries.BLOCKS.getValue(new ResourceLocation("grassslabs:dirt_stairs")));
    private static final Lazy<DirtCarpetBlock> DIRT_CARPET = Lazy.of(() -> (DirtCarpetBlock) ForgeRegistries.BLOCKS.getValue(new ResourceLocation("grassslabs:dirt_carpet")));
    private static final Lazy<DirtPathSlabBlock> DIRT_PATH_SLAB = Lazy.of(() -> (DirtPathSlabBlock) ForgeRegistries.BLOCKS.getValue(new ResourceLocation("grassslabs:dirt_path_slab")));
    private static final Lazy<DirtPathStairsBlock> DIRT_PATH_STAIRS = Lazy.of(() -> (DirtPathStairsBlock) ForgeRegistries.BLOCKS.getValue(new ResourceLocation("grassslabs:dirt_path_stairs")));
    private static final Lazy<DirtPathCarpetBlock> DIRT_PATH_CARPET = Lazy.of(() -> (DirtPathCarpetBlock) ForgeRegistries.BLOCKS.getValue(new ResourceLocation("grassslabs:dirt_path_carpet")));
    private static final Lazy<MyceliumSlabBlock> MYCELIUM_SLAB = Lazy.of(() -> (MyceliumSlabBlock) ForgeRegistries.BLOCKS.getValue(new ResourceLocation("grassslabs:mycelium_slab")));
    private static final Lazy<MyceliumStairsBlock> MYCELIUM_STAIRS = Lazy.of(() -> (MyceliumStairsBlock) ForgeRegistries.BLOCKS.getValue(new ResourceLocation("grassslabs:mycelium_stairs")));
    private static final Lazy<MyceliumCarpetBlock> MYCELIUM_CARPET = Lazy.of(() -> (MyceliumCarpetBlock) ForgeRegistries.BLOCKS.getValue(new ResourceLocation("grassslabs:mycelium_carpet")));

    private static Map<Block, Consumer<GrassSlabBlockContext>> PATHABLE = Maps.newHashMap(
        ImmutableMap.of(
            GRASS_SLAB.get(), determinePathingState(DIRT_PATH_SLAB.get()),
            GRASS_STAIRS.get(), determinePathingState(DIRT_PATH_STAIRS.get()),
            GRASS_CARPET.get(), determinePathingState(DIRT_PATH_CARPET.get()),
            DIRT_SLAB.get(), determinePathingState(DIRT_PATH_SLAB.get()),
            DIRT_STAIRS.get(), determinePathingState(DIRT_PATH_STAIRS.get()),
            DIRT_CARPET.get(), determinePathingState(DIRT_PATH_CARPET.get()),
            MYCELIUM_SLAB.get(), determinePathingState(DIRT_PATH_SLAB.get()),
            MYCELIUM_STAIRS.get(), determinePathingState(DIRT_PATH_STAIRS.get()),
            MYCELIUM_CARPET.get(), determinePathingState(DIRT_PATH_CARPET.get())
        )
    );

    @Override
    public void setup() {
        PathComponent.setProxy(this);
    }

    @Override
    public void setBlock(Level pLevel, BlockPos pBlockPos, BlockState pBlockState) {
        pLevel.setBlock(pBlockPos, pBlockState, 11);
    }

    @Override
    public @Nullable BlockState getPathingState(Level pLevel, BlockPos pBlock, BlockState pBlockState) {
        Consumer<GrassSlabBlockContext> consumer = PATHABLE.get(pBlockState.getBlock());
        if (consumer != null) {
            GrassSlabBlockContext context = new GrassSlabBlockContext(pLevel, pBlock, pBlockState);
            consumer.accept(context);
            return context.getNewState();
        }

        return ShovelItem.getShovelPathingState(pBlockState);
    }

    public static Consumer<GrassSlabBlockContext> determinePathingState(Block block) {
        return (context) -> {
            context.setNewState(block.withPropertiesOf(context.getOldState()));
        };
    }

    private static class GrassSlabBlockContext {
        BlockPos block;
        BlockState oldState;
        Level level;
        BlockState newState;

        GrassSlabBlockContext(Level level, BlockPos block, BlockState state) {
            this.level = level;
            this.block = block;
            this.oldState = state;
        }

        public BlockPos getBlock() {
            return this.block;
        }

        public Level getLevel() {
            return level;
        }

        public BlockState getNewState() {
            return newState;
        }

        public void setNewState(BlockState newState) {
            this.newState = newState;
        }

        public BlockState getOldState() {
            return oldState;
        }
    }
}
