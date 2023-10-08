/*
 *   Arcane Additions Copyright (c) 2023, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.events.spells;

import com.mna.effects.beneficial.EffectPossession;
import net.minecraft.util.profiling.jfr.event.WorldLoadFinishedEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.effects.EffectRegistry;
import org.sosly.arcaneadditions.effects.neutral.AstralProjectionEffect;
import org.sosly.arcaneadditions.entities.sorcery.AstralProjectionEntity;
import org.sosly.arcaneadditions.renderers.sorcery.AstralProjectionRenderer;

import java.util.Collection;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AstralProjectionEvents {

    @SubscribeEvent
    public static void onProjectionMoved(net.minecraftforge.event.entity.living.LivingEvent event) {
        if (event.getEntityLiving() instanceof AstralProjectionEntity projection) {
            if (projection.isOnGround() && projection.getPersistentData().getFloat("astral_magnitude") > 1.0F) {
                projection.setOnGround(false);
                projection.setNoGravity(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPotionRemoved(PotionEvent.PotionRemoveEvent event) {
        if (event.getPotion() instanceof EffectPossession) {
            LivingEntity entity = event.getEntityLiving();
            if (entity.level.isClientSide()) return;
            if (entity instanceof Player player) {
                player.removeEffect(EffectRegistry.ASTRAL_PROJECTION.get());
            }
        }

        runOnEffect(event, (instance, entity) -> {
            if (entity.level.isClientSide()) return;
            if (!(entity instanceof Player)) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        });
    }
    @SubscribeEvent
    public static void onPotionExpired(PotionEvent.PotionExpiryEvent event) {
        runOnEffect(event, (instance, entity) -> {
            if (entity.level.isClientSide()) return;
            if (entity instanceof Player) return;
            entity.remove(Entity.RemovalReason.DISCARDED);
        });
    }
    private static void runOnEffect(Event event, EffectRegistry.ILivingMobEffectInstanceHandler handler) {
        LivingEntity entity;

        if (event instanceof LivingEvent livingEvent) {
            entity = (LivingEntity) livingEvent.getEntity();
        } else if (event instanceof RenderLivingEvent<?, ?> livingEvent) {
            entity = livingEvent.getEntity();
        } else {
            return; // not sure how we got here but let's bail out just in case.
        }

        if (event instanceof PotionEvent.PotionAddedEvent) {
            MobEffectInstance instance = ((PotionEvent)event).getPotionEffect();
            MobEffect effect = instance.getEffect();
            if (effect instanceof AstralProjectionEffect) {
                EffectRegistry.handle(handler, instance, entity);
            }
        } else {
            Collection<MobEffectInstance> effects = entity.getActiveEffects();
            for (MobEffectInstance instance : effects) {
                MobEffect effect = instance.getEffect();
                if (effect instanceof AstralProjectionEffect) {
                    EffectRegistry.handle(handler, instance, entity);
                }
            }
        }
    }
}
