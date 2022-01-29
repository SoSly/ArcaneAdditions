package org.sosly.witchesandwizards.events;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.sosly.witchesandwizards.WitchesAndWizards;
import org.sosly.witchesandwizards.effects.beneficial.IceBlockEffect;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = WitchesAndWizards.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
class IceBlockEventHandler {
    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        LivingEntity entity = (LivingEntity)event.getEntity();
        Collection<MobEffectInstance> effects = entity.getActiveEffects();
        effects.forEach(instance -> {
            MobEffect effect = instance.getEffect();
            if (effect instanceof IceBlockEffect) {
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = (LivingEntity)event.getEntity();
        Collection<MobEffectInstance> effects = entity.getActiveEffects();
        effects.forEach(instance -> {
            MobEffect effect = instance.getEffect();
            if (effect instanceof IceBlockEffect) {
                entity.hasImpulse = false;
                entity.setDeltaMovement(0d, -2000d, 0d);
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerBreakingBlockCheck(PlayerEvent.HarvestCheck event) {
        LivingEntity entity = (LivingEntity)event.getEntity();
        Collection<MobEffectInstance> effects = entity.getActiveEffects();
        effects.forEach(instance -> {
            MobEffect effect = instance.getEffect();
            if (effect instanceof IceBlockEffect) {
                event.setCanHarvest(false);
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerBreakingBlockSpeed(PlayerEvent.BreakSpeed event) {
        LivingEntity entity = (LivingEntity)event.getEntity();
        Collection<MobEffectInstance> effects = entity.getActiveEffects();
        effects.forEach(instance -> {
            MobEffect effect = instance.getEffect();
            if (effect instanceof IceBlockEffect) {
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        LivingEntity entity = (LivingEntity)event.getEntity();
        Collection<MobEffectInstance> effects = entity.getActiveEffects();
        effects.forEach(instance -> {
            MobEffect effect = instance.getEffect();
            if (effect instanceof IceBlockEffect) {
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerInteractItem(PlayerInteractEvent.RightClickItem event) {
        LivingEntity entity = (LivingEntity)event.getEntity();
        Collection<MobEffectInstance> effects = entity.getActiveEffects();
        effects.forEach(instance -> {
            MobEffect effect = instance.getEffect();
            if (effect instanceof IceBlockEffect) {
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerPickup(PlayerEvent.ItemPickupEvent event) {
        LivingEntity entity = (LivingEntity)event.getEntity();
        Collection<MobEffectInstance> effects = entity.getActiveEffects();
        effects.forEach(instance -> {
            MobEffect effect = instance.getEffect();
            if (effect instanceof IceBlockEffect) {
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent
    public static void onInteractAtBlock(PlayerInteractEvent.RightClickBlock event) {
        LivingEntity entity = (LivingEntity)event.getEntity();
        Collection<MobEffectInstance> effects = entity.getActiveEffects();
        effects.forEach(instance -> {
            MobEffect effect = instance.getEffect();
            if (effect instanceof IceBlockEffect) {
                event.setUseItem(Event.Result.DENY);
            }
        });
    }
}
