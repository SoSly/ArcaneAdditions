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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.sosly.arcaneadditions.compat.Grass_Slabs.DummyPathableBlockProxy;
import org.sosly.arcaneadditions.compat.Grass_Slabs.IPathableBlockProxy;

public class PathComponent extends SpellEffect {
    public static IPathableBlockProxy proxy = new DummyPathableBlockProxy();

    public PathComponent(ResourceLocation registryName, ResourceLocation guiIcon) {
        super(registryName, guiIcon);
    }

    @Override
    public ComponentApplicationResult ApplyEffect(SpellSource caster, SpellTarget target, IModifiedSpellPart<SpellEffect> mods, SpellContext context) {
        if (target.isBlock()) {
            Level level = context.getWorld();
            BlockPos pos = target.getBlock();
            BlockState state = level.getBlockState(target.getBlock());

            if (!context.getWorld().isEmptyBlock(target.getBlock()) && context.getWorld().getFluidState(target.getBlock()).isEmpty() && !(state.getBlock() instanceof EntityBlock)) {
                BlockState state1 = PathComponent.proxy.getPathingState(level, pos, state);

                if (state1 != null && level.isEmptyBlock(pos.above())) {
                    if (!level.isClientSide) {
                        PathComponent.proxy.setBlock(level, pos, state1);
                    }
                }
            }
        }

        return ComponentApplicationResult.FAIL;
    }

    @Override
    public Affinity getAffinity() {
        return Affinity.EARTH;
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

    public static void setProxy(IPathableBlockProxy proxy) {
        PathComponent.proxy = proxy;
    }

    @Override
    public SoundEvent SoundEffect() {
        return SFX.Spell.Cast.EARTH;
    }
}
