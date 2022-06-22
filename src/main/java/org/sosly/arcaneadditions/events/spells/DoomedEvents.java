/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.events.spells;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.effects.EffectRegistry;
import org.sosly.arcaneadditions.effects.beneficial.LifeLinkEffect;
import org.sosly.arcaneadditions.effects.harmful.DoomedEffect;
import org.sosly.arcaneadditions.spells.components.LifeLinkComponent;

@Mod.EventBusSubscriber(modid = ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DoomedEvents {
    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        runOnEffect(event, (instance,  entity) -> {
            // damage formula mimics Sharpness
            double damage = event.getAmount() + (0.5 * instance.getAmplifier()) + 0.5;
            event.setAmount((float)damage);
        });
    }

    private static void runOnEffect(Event event, EffectRegistry.ILivingMobEffectInstanceHandler handler) {
        LivingEntity entity;

        if (event instanceof LivingEvent livingEvent) {
            entity = livingEvent.getEntityLiving();
        } else if (event instanceof RenderLivingEvent<?,?> livingEvent) {
            entity = livingEvent.getEntity();
        } else {
            return;
        }

        for (MobEffectInstance instance : entity.getActiveEffects()) {
            if (instance.getEffect() instanceof DoomedEffect) {
                EffectRegistry.handle(handler, instance, entity);
            }
        }
    }
}
