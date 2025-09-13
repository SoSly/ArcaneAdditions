/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.compats.magichem;

import com.aranaira.magichem.util.InteropUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.sosly.arcaneadditions.compats.ICompat;

public class MagiChemCompat implements ICompat {

    @Override
    public void setup() {
    }

    public static void tryGenerateVerdigris(Level level, BlockPos pos, BlockState state, BlockHitResult hitResult) {
        if (!isCopperBlock(state)) {
            return;
        }

        InteropUtil.tryGenerateVerdigris(level, pos, hitResult);
    }

    private static boolean isCopperBlock(BlockState state) {
        return state.getBlock() instanceof WeatheringCopper;
    }
}
