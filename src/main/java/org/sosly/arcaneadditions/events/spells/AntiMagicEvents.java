/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.events.spells;

import com.mna.api.events.ComponentApplyingEvent;
import com.mna.api.spells.targeting.SpellTarget;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.effects.beneficial.AntiMagicEffect;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AntiMagicEvents {
    @SubscribeEvent
    public static void onSpellComponent(ComponentApplyingEvent event) {
        SpellTarget target = event.getTarget();
        if (target.isLivingEntity()) {
            LivingEntity entity = target.getLivingEntity();
            if (entity != null) {
                Level level = entity.getLevel();
                Collection<MobEffectInstance> effects = entity.getActiveEffects();
                if (!effects.isEmpty()) {
                    effects.stream()
                            .filter(effect -> effect.getEffect() instanceof AntiMagicEffect)
                            .findAny()
                            .ifPresent(effect -> {
                                int tier = event.getComponent().getTier(level);
                                if (effect.getAmplifier() + 1 >= tier) {
                                    event.setCanceled(true);
                                    entity.removeEffect(effect.getEffect());
                                }
                            });
                }
            }
        }
    }
}
