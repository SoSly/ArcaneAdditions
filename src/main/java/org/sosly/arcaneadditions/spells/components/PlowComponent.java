/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.spells.components;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mna.api.affinity.Affinity;
import com.mna.api.sound.SFX;
import com.mna.api.spells.ComponentApplicationResult;
import com.mna.api.spells.SpellPartTags;
import com.mna.api.spells.base.IModifiedSpellPart;
import com.mna.api.spells.parts.SpellEffect;
import com.mna.api.spells.targeting.SpellContext;
import com.mna.api.spells.targeting.SpellSource;
import com.mna.api.spells.targeting.SpellTarget;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PlowComponent extends SpellEffect {
    public PlowComponent(ResourceLocation guiIcon) {
        super(guiIcon);
    }

    @Override
    public ComponentApplicationResult ApplyEffect(SpellSource caster, SpellTarget target, IModifiedSpellPart<SpellEffect> mods, SpellContext context) {
        if (target.isBlock()) {
            Level level = context.getWorld();
            BlockPos block = target.getBlock();
            BlockState state = level.getBlockState(target.getBlock());

            if (!context.getWorld().isEmptyBlock(target.getBlock()) && context.getWorld().getFluidState(target.getBlock()).isEmpty() && !(state.getBlock() instanceof EntityBlock)) {
                Pair<Predicate<SpellBlockContext>, Consumer<SpellBlockContext>> pair = TILLABLES.get(state.getBlock());
                if (pair != null) {
                    Predicate<SpellBlockContext> predicate = pair.getFirst();
                    Consumer<SpellBlockContext> consumer = pair.getSecond();
                    SpellBlockContext sbx = new SpellBlockContext(level, block);

                    if (predicate.test(sbx)) {
                        if (!level.isClientSide) {
                            consumer.accept(sbx);
                        }
                    }
                }
            }
        }

        return ComponentApplicationResult.FAIL;
    }

    @Override
    public Affinity getAffinity() {
        return Affinity.WATER;
    }

    @Override
    public SpellPartTags getUseTag() {
        return SpellPartTags.UTILITY;
    }

    @Override
    public float initialComplexity() {
        return 5.0F;
    }

    @Override
    public int requiredXPForRote() {
        return 100;
    }

    @Override
    public SoundEvent SoundEffect() {
        return SFX.Spell.Cast.WATER;
    }

    // todo: Hoe items are broken in Forge, and the behavior to check for it is incredibly hard to replicate.
    //       Therefore, this hacky solution is in use for now, but it should be improved if Hoe items are ever fixed.
    private static final Map<Block, Pair<Predicate<SpellBlockContext>, Consumer<SpellBlockContext>>> TILLABLES = Maps.newHashMap(
            ImmutableMap.of(
                    Blocks.GRASS_BLOCK, Pair.of(PlowComponent::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())),
                    Blocks.DIRT_PATH, Pair.of(PlowComponent::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())),
                    Blocks.DIRT, Pair.of(PlowComponent::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())),
                    Blocks.COARSE_DIRT, Pair.of(PlowComponent::onlyIfAirAbove, changeIntoState(Blocks.DIRT.defaultBlockState())),
                    Blocks.ROOTED_DIRT, Pair.of((context) -> true, changeIntoStateAndDropItem(Blocks.DIRT.defaultBlockState(), Items.HANGING_ROOTS))
            )
    );

    public static boolean onlyIfAirAbove(SpellBlockContext context) {
        return context.getLevel().isEmptyBlock(context.getBlock().above());
    }

    public static Consumer<SpellBlockContext> changeIntoState(BlockState state) {
        return (context) -> {
            context.getLevel().setBlock(context.getBlock(), state, 11);
        };
    }

    public static Consumer<SpellBlockContext> changeIntoStateAndDropItem(BlockState state, ItemLike itemToDrop) {
        return (context) -> {
            context.getLevel().setBlock(context.getBlock(), state, 11);
            Block.popResource(context.getLevel(), context.getBlock(), new ItemStack(itemToDrop));
        };
    }

    private static class SpellBlockContext {
        Level level;
        BlockPos block;

        SpellBlockContext(Level level, BlockPos block) {
            this.level = level;
            this.block = block;
        }

        public BlockPos getBlock() { return this.block; }
        public Level getLevel() {
            return this.level;
        }
    }
}
