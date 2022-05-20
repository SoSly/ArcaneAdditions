/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.effects.beneficial;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.sosly.arcaneadditions.spells.components.IceBlockComponent;

public class IceBlockEffect extends MobEffect {
    private static final int BASE_FREQUENCY = 20;

    public IceBlockEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
        this.addAttributeModifier(Attributes.FLYING_SPEED, "433ded4e-7346-45aa-bfa2-7016000336e8", -2000D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "f06ef7d3-501e-4221-a6fa-23f2980630df", -2000D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Level level = entity.getLevel();
        int iceBlocKID = entity.getPersistentData().getInt(IceBlockComponent.ICEBLOCK_ENTITY_ID);
        Entity iceBlock = level.getEntity(iceBlocKID);

        if (iceBlock != null) {
            double x = entity.getX() - 0.5f;
            double y = entity.getY();
            double z = entity.getZ() - 0.5f;

            iceBlock.setPos(x, y, z);
            iceBlock.setXRot(entity.getXRot());
            iceBlock.setYRot(entity.getYRot());
        }

        if (entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(1.0f);
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
}
