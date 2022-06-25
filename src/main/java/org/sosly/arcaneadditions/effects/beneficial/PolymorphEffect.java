/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.effects.beneficial;

import com.mna.api.ManaAndArtificeMod;
import com.mna.api.capabilities.resource.ICastingResource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.sosly.arcaneadditions.capabilities.polymorph.PolymorphProvider;
import org.sosly.arcaneadditions.compats.BMorph.BMorphRegistryEntries;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class PolymorphEffect extends MobEffect {
    public PolymorphEffect() {
        super(MobEffectCategory.NEUTRAL, 0);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player)) return; // for now, we can't affect non-players

        entity.getCapability(PolymorphProvider.POLYMORPH).ifPresent(polymorph -> {
            float complexity = polymorph.getComplexity();
            WeakReference<Player> caster = polymorph.getCaster(entity.getLevel());

            if (caster == null) {
                entity.removeEffect(BMorphRegistryEntries.POLYMORPH_EFFECT);
                return;
            }



            Objects.requireNonNull(caster.get()).getCapability(ManaAndArtificeMod.getMagicCapability(), null).ifPresent(magic -> {
                ICastingResource resource = magic.getCastingResource();
                if (resource.getAmount() < complexity) {
                    entity.removeEffect(BMorphRegistryEntries.POLYMORPH_EFFECT);
                } else {
                    resource.consume(entity, complexity);
                }
            });
        });
    }

    @Override
    public boolean isDurationEffectTick(int durationTicks, int amplifier) {
        int MANA_COST_FREQUENCY = 20;
        return durationTicks % MANA_COST_FREQUENCY == 0;
    }
}
