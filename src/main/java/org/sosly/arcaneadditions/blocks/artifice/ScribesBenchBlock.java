package org.sosly.arcaneadditions.blocks.artifice;

import com.mna.api.blocks.WizardLabBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.sosly.arcaneadditions.blocks.TileEntityRegistry;
import org.sosly.arcaneadditions.blocks.tileentities.ScribesBenchTile;
import org.sosly.arcaneadditions.gui.menus.ScribesBenchMenu;
import org.sosly.arcaneadditions.utils.RLoc;

public class ScribesBenchBlock extends WizardLabBlock implements EntityBlock {
    private static final Component CONTAINER_TITLE = Component.translatable(RLoc.create("container.scribes_bench").toString());

    public ScribesBenchBlock() {
        super(Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).noOcclusion().strength(3.0F));
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ScribesBenchTile(pos, state);
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == TileEntityRegistry.SCRIBES_BENCH.get() ? (lvl, pos, state1, be) -> {
            ScribesBenchTile.Tick(lvl, pos, state1, (ScribesBenchTile)be);
        } : null;
    }

    protected MenuProvider getProvider(BlockState state, Level level, BlockPos pos, Player player,
                                       InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity be = level.getBlockEntity(pos);
        return be != null && be instanceof ScribesBenchTile tile ? new SimpleMenuProvider((id, playerInv, user) ->
                new ScribesBenchMenu(id, playerInv, tile), CONTAINER_TITLE) : null;
    }
}
