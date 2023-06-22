/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.spells.components;

import com.mna.api.affinity.Affinity;
import com.mna.api.faction.IFaction;
import com.mna.api.sound.SFX;
import com.mna.api.spells.ComponentApplicationResult;
import com.mna.api.spells.SpellPartTags;
import com.mna.api.spells.attributes.Attribute;
import com.mna.api.spells.attributes.AttributeValuePair;
import com.mna.api.spells.base.IDamageComponent;
import com.mna.api.spells.base.IModifiedSpellPart;
import com.mna.api.spells.parts.SpellEffect;
import com.mna.api.spells.targeting.SpellContext;
import com.mna.api.spells.targeting.SpellSource;
import com.mna.api.spells.targeting.SpellTarget;
import com.mna.config.GeneralModConfig;
import com.mna.factions.Factions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class TransfuseComponent extends SpellEffect implements IDamageComponent {
    public TransfuseComponent(ResourceLocation registryName, ResourceLocation guiIcon) {
        super(registryName, guiIcon,
                new AttributeValuePair(Attribute.DAMAGE, 2.0F, 1.0F, 10.0F, 0.5F, 20.0F),
                new AttributeValuePair(Attribute.MAGNITUDE, 1.0F, 1.0F, 5.0F, 1.0F, 20.0F));
    }

    @Override
    public ComponentApplicationResult ApplyEffect(SpellSource source, SpellTarget target, IModifiedSpellPart<SpellEffect> mods, SpellContext context) {
        LivingEntity livingTarget = null;
        LivingEntity livingSource = null;
        float damage = mods.getValue(Attribute.DAMAGE) * GeneralModConfig.getDamageMultiplier();
        float magnitude = mods.getValue(Attribute.MAGNITUDE);
        float healing = damage * (magnitude / 5);

        if (target.isLivingEntity() && target.getLivingEntity() != null) {
            livingTarget = target.getLivingEntity();
        }
        if (source.getCaster() != null) {
            livingSource = source.getCaster();
        }

        if (livingTarget == null || livingSource == null) {
            return ComponentApplicationResult.NOT_PRESENT;
        }

        if (livingSource.isCrouching()) {
            // hurt me to heal them
            boolean hurt = livingSource.hurt(DamageSource.WITHER, damage);
            if (!hurt) {
                return ComponentApplicationResult.FAIL;
            }
            livingTarget.heal(healing);
        } else {
            // hurt them to heal me
            boolean hurt = livingTarget.hurt(DamageSource.WITHER, damage);
            if (!hurt) {
                return ComponentApplicationResult.FAIL;
            }
            livingSource.heal(healing);
        }

        return ComponentApplicationResult.NOT_PRESENT;
    }

    @Override
    public SoundEvent SoundEffect() {
        return SFX.Spell.Impact.Single.ARCANE;
    }

    @Override
    public Affinity getAffinity() {
        return Affinity.ARCANE;
    }

    @Override
    public SpellPartTags getUseTag() {
        return SpellPartTags.HARMFUL;
    }

    @Override
    public float initialComplexity() {
        return 50.0F;
    }

    @Override
    public int requiredXPForRote() {
        return 100;
    }

    @Override
    public IFaction getFactionRequirement() { return Factions.UNDEAD; }

    @Override
    public boolean targetsBlocks() {
        return false;
    }
}
