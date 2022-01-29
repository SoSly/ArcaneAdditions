package org.sosly.witchesandwizards.effects.beneficial;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.sosly.witchesandwizards.effects.EffectRegistry;
import org.sosly.witchesandwizards.effects.neutral.IceBlockExhaustionEffect;

public class IceBlockEffect extends MobEffect {
    private static final int BASE_FREQUENCY = 20;

    public IceBlockEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
        this.addAttributeModifier(Attributes.FLYING_SPEED, "433ded4e-7346-45aa-bfa2-7016000336e8", -2000D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "f06ef7d3-501e-4221-a6fa-23f2980630df", -2000D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        entity.setInvulnerable(true);
        if (entity instanceof Player) {
            entity.setTicksFrozen(Integer.MAX_VALUE);
        }
        super.addAttributeModifiers(entity, attributeMap, amplifier);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(1.0F);
        }
    }

    @Override
    public boolean isDurationEffectTick(int durationTicks, int amplifier) {
        int frequency = BASE_FREQUENCY >> amplifier;
        if (frequency > 0) {
            return durationTicks % frequency == 0;
        } else {
            return true;
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        entity.setInvulnerable(false);
        if (entity instanceof Player) {
            entity.setTicksFrozen(0);
        }
        MobEffectInstance cooldown = new MobEffectInstance(EffectRegistry.ICE_BLOCK_EXHAUSTION.get(), 600, 0);
        entity.addEffect(cooldown);
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
    }
}
