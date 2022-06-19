/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.effects.harmful;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.sosly.arcaneadditions.spells.components.EnrageComponent;

public class EnragedEffect extends MobEffect {
    public EnragedEffect() {
        super(MobEffectCategory.HARMFUL, 0);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "0468794e-058d-4216-99a9-2f6f844197bf", 1, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "0468794e-058d-4216-99a9-2f6f844197bf", 0.05, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, "0468794e-058d-4216-99a9-2f6f844197bf", 1, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        int sourceID = entity.getPersistentData().getInt(EnrageComponent.CASTER);
        if (entity instanceof  Mob mob) {
            entity.getLevel().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(10.0f),
                    (e) -> e.isAlive() && e.getId() == sourceID).stream().findFirst().ifPresent(mob::setTarget);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
