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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import org.sosly.arcaneadditions.effects.EffectRegistry;

import java.util.Objects;

public class IceBlockComponent extends SpellEffect {

    public IceBlockComponent(ResourceLocation registryName, ResourceLocation guiIcon) {
        super(registryName, guiIcon, new AttributeValuePair(Attribute.DURATION, 10.0F, 2.0F, 30.0F, 2.0F, 2.0F));
    }

    @Override
    public ComponentApplicationResult ApplyEffect(SpellSource source, SpellTarget target, IModifiedSpellPart<SpellEffect> mods, SpellContext context) {
        if (target.isLivingEntity() && Objects.requireNonNull(target.getLivingEntity()).getEffect(EffectRegistry.ICE_BLOCK_EXHAUSTION.get()) == null && target.getLivingEntity().getEffect(EffectRegistry.ICE_BLOCK.get()) == null) {
            target.getLivingEntity().addEffect(new MobEffectInstance(EffectRegistry.ICE_BLOCK.get(), (int)mods.getValue(Attribute.DURATION) * 20, 0));
            return ComponentApplicationResult.SUCCESS;
        }

        return ComponentApplicationResult.FAIL;
    }

    @Override
    public boolean canBeChanneled() {
        return false;
    }

    @Override
    public Affinity getAffinity() {
        return Affinity.ICE;
    }

    @Override
    public SpellPartTags getUseTag() {
        return SpellPartTags.UTILITY;
    }

    @Override
    public float initialComplexity() {
        return 20.0f;
    }

    @Override
    public int requiredXPForRote() {
        return 100;
    }

    @Override
    public SoundEvent SoundEffect() {
        return SFX.Spell.Buff.ICE;
    }
}
