/*
 *   Arcane Additions Copyright (c) 2023, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.spells.components;

import com.mna.api.affinity.Affinity;
import com.mna.api.faction.IFaction;
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
import com.mna.effects.EffectInit;
import com.mna.factions.Factions;
import com.mna.network.ServerMessageDispatcher;
import com.mna.spells.components.PotionEffectComponent;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.sosly.arcaneadditions.effects.EffectRegistry;
import org.sosly.arcaneadditions.entities.sorcery.AstralProjectionEntity;

public class AstralProjectionComponent extends PotionEffectComponent {
    public AstralProjectionComponent(ResourceLocation registryName, ResourceLocation guiIcon) {
        super(registryName, guiIcon, EffectRegistry.ASTRAL_PROJECTION,
                new AttributeValuePair(Attribute.MAGNITUDE, 1.0F, 1.0F, 3.0F, 1.0F, 20.0F),
                new AttributeValuePair(Attribute.DURATION, 15.0F, 15.0F, 120.0F, 15.0F, 10.0F),
                new AttributeValuePair(Attribute.SPEED, 0.3F, 0.3F, 1.0F, 0.1F, 1.0F)
        );
    }

    @Override
    public ComponentApplicationResult ApplyEffect(SpellSource caster, SpellTarget target, IModifiedSpellPart<SpellEffect> mods, SpellContext context) {
        if (target.getLivingEntity() == null || !(target.getEntity() instanceof Player controller)) {
            return ComponentApplicationResult.FAIL;
        }

        assert context.getServerWorld() != null;
        if (context.getServerWorld().isClientSide() || !caster.isPlayerCaster()) {
            return ComponentApplicationResult.FAIL;
        }

        if (controller.getEffect(EffectInit.MIND_VISION.get()) != null) {
            return ComponentApplicationResult.FAIL;
        }

        int duration = (int) (mods.getValue(Attribute.DURATION) * 20.0F);

        Level level = context.getWorld();

        AstralProjectionEntity projection = new AstralProjectionEntity(controller, level);

        projection.setPose(Pose.STANDING);
        projection.copyPosition(controller);
        projection.addTag("arcaneadditions:astral_projection");

        projection.getPersistentData().putUUID("astral_controller_id", controller.getUUID());
        projection.getPersistentData().putInt("astral_duration", duration);
        projection.getPersistentData().putFloat("astral_magnitude", mods.getValue(Attribute.MAGNITUDE));
        controller.getPersistentData().putInt("astral_entity_id", projection.getId());

        projection.setSpeed(mods.getValue(Attribute.SPEED));
        if (mods.getValue(Attribute.MAGNITUDE) > 1.0F) {
            projection.setOnGround(false);
            projection.setNoGravity(true);
        }
        projection.setInvulnerable(true);
        if (mods.getValue(Attribute.MAGNITUDE) > 2.0F) {
            projection.noPhysics = true;
        }
        level.addFreshEntity(projection);
        target.overrideSpellTarget(projection);

        controller.addEffect(new MobEffectInstance(EffectInit.POSSESSION.get(), duration, 1));
        projection.addEffect(new MobEffectInstance(EffectInit.POSSESSION.get(), duration));
        controller.addEffect(new MobEffectInstance(EffectRegistry.ASTRAL_PROJECTION.get(), duration));
        projection.addEffect(new MobEffectInstance(EffectRegistry.ASTRAL_PROJECTION.get(), duration));

        ServerMessageDispatcher.sendPlayerPosessionMessage((ServerPlayer) controller, projection);
        controller.getPersistentData().putInt("posessed_entity_id", projection.getId());

        return ComponentApplicationResult.DELAYED;
    }

    @Override
    public boolean canBeChanneled() {
        return false;
    }

    @Override
    public SoundEvent SoundEffect() {
        return SFX.Spell.Cast.BRIAN;
    }

    @Override
    public Affinity getAffinity() {
        return Affinity.ARCANE;
    }

    @Override
    public IFaction getFactionRequirement() {
        return Factions.COUNCIL;
    }

    @Override
    public float initialComplexity() {
        return 50.0F;
    }

    @Override
    public int requiredXPForRote() {
        return 300;
    }

    @Override
    public void SpawnParticles(Level world, Vec3 impact_position, Vec3 normal, int age, LivingEntity caster, ISpellDefinition recipe) {
        if (age <= 10) {
            float particle_spread = 1.0F;
            float v = 0.4F;
            int particleCount = 10;

            for(int i = 0; i < particleCount; ++i) {
                Vec3 velocity = new Vec3(0.0, Math.random() * (double)v, 0.0);
                world.addParticle(recipe.colorParticle((new MAParticleType((ParticleType) ParticleInit.ARCANE_RANDOM.get())).setScale(0.2F).setColor(10, 10, 10), caster), impact_position.x + (double)(-particle_spread) + Math.random() * (double)particle_spread * 2.0, impact_position.y + (double)(-particle_spread) + Math.random() * (double)particle_spread * 2.0, impact_position.z + (double)(-particle_spread) + Math.random() * (double)particle_spread * 2.0, velocity.x, velocity.y, velocity.z);
            }

        }
    }

    @Override
    public SpellPartTags getUseTag() {
        return SpellPartTags.UTILITY;
    }
}
