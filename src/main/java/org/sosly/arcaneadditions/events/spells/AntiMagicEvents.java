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
                Level level = entity.level();
                Collection<MobEffectInstance> effects = entity.getActiveEffects();
                if (!effects.isEmpty()) {
                    for (MobEffectInstance effect : effects) {
                        if (effect.getEffect() instanceof AntiMagicEffect) {
                            int tier = event.getComponent().getTier(level);
                            if (effect.getAmplifier() + 1 >= tier) {
                                event.setCanceled(true);
                                entity.removeEffect(effect.getEffect());
                            }
                        }
                    }
                }
            }
        }
    }
}
