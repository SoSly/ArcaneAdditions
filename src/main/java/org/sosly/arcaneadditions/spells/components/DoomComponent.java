/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.spells.components;

import com.mna.api.affinity.Affinity;
import com.mna.api.particles.MAParticleType;
import com.mna.api.particles.ParticleInit;
import com.mna.api.sound.SFX;
import com.mna.api.spells.ComponentApplicationResult;
import com.mna.api.spells.SpellPartTags;
import com.mna.api.spells.attributes.Attribute;
import com.mna.api.spells.attributes.AttributeValuePair;
import com.mna.api.spells.base.IModifiedSpellPart;
import com.mna.api.spells.base.ISpellDefinition;
import com.mna.api.spells.parts.SpellEffect;
import com.mna.api.spells.targeting.SpellContext;
import com.mna.api.spells.targeting.SpellSource;
import com.mna.api.spells.targeting.SpellTarget;
import com.mna.spells.components.PotionEffectComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.sosly.arcaneadditions.effects.EffectRegistry;

public class DoomComponent extends PotionEffectComponent {
    public DoomComponent(ResourceLocation registryName, ResourceLocation guiIcon) {
        super(registryName, guiIcon, EffectRegistry.DOOMED,
                new AttributeValuePair(Attribute.MAGNITUDE, 2.0F, 2.0F, 6.0F, 1.0F, 25.0F),
                new AttributeValuePair(Attribute.DURATION, 5.0F, 5.0F, 60.0F, 5.0F, 5.0F));
    }

    @Override
    public SoundEvent SoundEffect() {
        return SFX.Spell.Impact.Single.ENDER;
    }

    @Override
    public Affinity getAffinity() {
        return Affinity.ENDER;
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
    public void SpawnParticles(Level world, Vec3 impact_position, int age, Player caster, ISpellDefinition recipe) {
        if (age <= 10) {
            float particle_spread = 1.0F;
            float v = 0.4F;
            int particleCount = 10;

            for(int i = 0; i < particleCount; ++i) {
                Vec3 velocity = new Vec3(0.0, -Math.random() * (double)v, 0.0);
                world.addParticle(recipe.colorParticle(new MAParticleType(ParticleInit.ENDER.get()), caster), impact_position.x + (double)(-particle_spread) + Math.random() * (double)particle_spread * 2.0, impact_position.y + (double)(-particle_spread) + Math.random() * (double)particle_spread * 2.0, impact_position.z + (double)(-particle_spread) + Math.random() * (double)particle_spread * 2.0, velocity.x, velocity.y, velocity.z);
            }
        }
    }

    @Override
    public boolean targetsBlocks() {
        return false;
    }
}
