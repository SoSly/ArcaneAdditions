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
import com.mna.api.spells.attributes.Attribute;
import com.mna.api.spells.attributes.AttributeValuePair;
import com.mna.api.spells.base.IModifiedSpellPart;
import com.mna.api.spells.parts.SpellEffect;
import com.mna.api.spells.targeting.SpellContext;
import com.mna.api.spells.targeting.SpellSource;
import com.mna.api.spells.targeting.SpellTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.sosly.arcaneadditions.compats.Grass_Slabs.DummyPathableBlockProxy;
import org.sosly.arcaneadditions.compats.Grass_Slabs.IPathableBlockProxy;

public class PathComponent extends SpellEffect {
    public static IPathableBlockProxy proxy = new DummyPathableBlockProxy();
    private static final ResourceLocation PILGRIM = new ResourceLocation("mna", "pilgrim");

    public PathComponent(ResourceLocation guiIcon) {
        super(guiIcon, new AttributeValuePair(Attribute.DURATION, 30.0F, 30.0F, 600.0F, 30.0F, 10.0F));
    }

    @Override
    public ComponentApplicationResult ApplyEffect(SpellSource caster, SpellTarget target, IModifiedSpellPart<SpellEffect> mods, SpellContext context) {
        if (target.isBlock()) {
            Level level = context.getLevel();
            BlockPos pos = target.getBlock();
            BlockState state = level.getBlockState(target.getBlock());

            if (!context.getLevel().isEmptyBlock(target.getBlock()) && context.getLevel().getFluidState(target.getBlock()).isEmpty() && !(state.getBlock() instanceof EntityBlock)) {
                BlockState state1 = PathComponent.proxy.getPathingState(level, pos, state);

                if (state1 != null && level.isEmptyBlock(pos.above())) {
                    if (!level.isClientSide) {
                        PathComponent.proxy.setBlock(level, pos, state1);
                    }
                }
            }
        }

        // Pilgrim's Step when cast on self
        LivingEntity targetEntity = target.getLivingEntity();
        LivingEntity casterEntity = caster.getCaster();
        MobEffect pilgrim = ForgeRegistries.MOB_EFFECTS.getValue(PILGRIM);
        if (targetEntity != null && targetEntity.equals(casterEntity) && pilgrim != null) {
            casterEntity.addEffect(new MobEffectInstance(pilgrim, (int) mods.getValue(Attribute.DURATION) * 20, 0, false, false));
            return ComponentApplicationResult.SUCCESS;
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
