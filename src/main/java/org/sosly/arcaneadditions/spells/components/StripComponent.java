/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.spells.components;

import com.mna.api.affinity.Affinity;
import com.mna.api.sound.SFX;
import com.mna.api.spells.ComponentApplicationResult;
import com.mna.api.spells.SpellPartTags;
import com.mna.api.spells.base.IModifiedSpellPart;
import com.mna.api.spells.parts.SpellEffect;
import com.mna.api.spells.targeting.SpellContext;
import com.mna.api.spells.targeting.SpellSource;
import com.mna.api.spells.targeting.SpellTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class StripComponent extends SpellEffect {
    public StripComponent(ResourceLocation registryName, ResourceLocation guiIcon) {
        super(registryName, guiIcon);
    }

    @Override
    public ComponentApplicationResult ApplyEffect(SpellSource caster, SpellTarget target, IModifiedSpellPart<SpellEffect> mods, SpellContext context) {
        if (target.isBlock()) {
            Level level = context.getWorld();
            BlockPos pos = target.getBlock();
            BlockState state = level.getBlockState(target.getBlock());

            if (!context.getWorld().isEmptyBlock(target.getBlock()) && context.getWorld().getFluidState(target.getBlock()).isEmpty() && !(state.getBlock() instanceof EntityBlock)) {
                Optional<BlockState> strippable = Optional.ofNullable(AxeItem.getAxeStrippingState(state));
                Optional<BlockState> scrapable = WeatheringCopper.getPrevious(state);
                Optional<BlockState> waxed = Optional.ofNullable(HoneycombItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock())).map((waxable) -> waxable.withPropertiesOf(state));
                Optional<BlockState> result = Optional.empty();

                if (strippable.isPresent()) {
                    result = strippable;
                } else if (scrapable.isPresent()) {
                    result = scrapable;
                } else if (waxed.isPresent()) {
                    result = waxed;
                }

                result.ifPresent(blockState -> level.setBlock(pos, blockState, 11));
            }
        }

        return ComponentApplicationResult.FAIL;
    }

    @Override
    public Affinity getAffinity() {
        return Affinity.WIND;
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
        return SFX.Spell.Cast.WIND;
    }
}
