/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.compats.Grass_Slabs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DummyPathableBlockProxy implements IPathableBlockProxy {
    @Override
    public void setBlock(Level pLevel, BlockPos pBlockPos, BlockState pBlockState) {
        pLevel.setBlock(pBlockPos, pBlockState, 11);
    }

    @Override
    public @Nullable BlockState getPathingState(Level pLevel, BlockPos pBlock, BlockState pBlockState) {
        return ShovelItem.getShovelPathingState(pBlockState);
    }
}
