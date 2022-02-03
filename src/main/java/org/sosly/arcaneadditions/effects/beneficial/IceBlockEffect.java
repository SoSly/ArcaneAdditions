package org.sosly.arcaneadditions.effects.beneficial;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import org.sosly.arcaneadditions.client.entity.EntityRegistry;
import org.sosly.arcaneadditions.client.entity.IceBlockEntity;
import org.sosly.arcaneadditions.effects.EffectRegistry;
import org.sosly.arcaneadditions.utils.World;

import java.util.Collection;

public class IceBlockEffect extends MobEffect {
    private static final int BASE_FREQUENCY = 20;

    public IceBlockEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
        this.addAttributeModifier(Attributes.FLYING_SPEED, "433ded4e-7346-45aa-bfa2-7016000336e8", -2000D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "f06ef7d3-501e-4221-a6fa-23f2980630df", -2000D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
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

    public static void handleDamageEvents(LivingDamageEvent event) {
        if (event.getEntity().level.isClientSide()) return;

        runOnEffect(event, (instance, entity) -> {
            event.setCanceled(true);
            event.setAmount(0f);
            event.setResult(Event.Result.DENY);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleRenderEvent(RenderLivingEvent event) {
        runOnEffect(event, (instance, entity) -> {
            IceBlockEntity ice = new IceBlockEntity(EntityRegistry.ICE_BLOCK.get(), entity.getLevel());
            PoseStack stack = event.getPoseStack();
            EntityDimensions dim = entity.getDimensions(entity.getPose());
            stack.pushPose();
            stack.scale(dim.width, dim.height/3.0f, dim.width);
            Minecraft.getInstance().getEntityRenderDispatcher().render(ice, -0.5f, 0, -0.5f, 0, event.getPartialTick(), stack, event.getMultiBufferSource(), event.getPackedLight());
            stack.popPose();
        });
    }

    public static void handleRestrictedActions(LivingEvent event) {
        runOnEffect(event, (instance, entity) -> {
            if (event.isCancelable()) event.setCanceled(true);
            if (event instanceof PlayerInteractEvent) ((PlayerInteractEvent)event).setResult(Event.Result.DENY);
            if (event instanceof PlayerEvent.HarvestCheck) ((PlayerEvent.HarvestCheck)event).setCanHarvest(false);
            if (event instanceof LivingEvent.LivingJumpEvent) {
                entity.hasImpulse = false;
                entity.setDeltaMovement(0d, -2000d, 0d);
            }
        });
    }

    public static void onEffectAdded(PotionEvent event) {
        runOnEffect(event, (instance, entity) -> {
            syncEffect(entity, instance, true);
            entity.setNoActionTime(Integer.MAX_VALUE);
            entity.setTicksFrozen(Integer.MAX_VALUE);
        });
    }

    public static void onEffectRemoved(PotionEvent event) {
        runOnEffect(event, (instance, entity) -> {
            syncEffect(entity, instance, false);
            entity.setNoActionTime(0);
            entity.setTicksFrozen(0);

            Minecraft.getInstance().execute(() -> {
                MobEffectInstance cooldown = new MobEffectInstance(EffectRegistry.ICE_BLOCK_EXHAUSTION.get(), 600, 0);
                entity.addEffect(cooldown);
            });
        });
    }

    private static void runOnEffect(Event event, EffectRegistry.ILivingMobEffectInstanceHandler handler) {
        LivingEntity entity;

        if (event instanceof LivingEvent) {
            entity = (LivingEntity)((LivingEvent)event).getEntity();
        } else if (event instanceof RenderLivingEvent) {
            entity = ((RenderLivingEvent)event).getEntity();
        } else {
            return; // not sure how we got here but let's bail out just in case.
        }

        if (event instanceof PotionEvent.PotionAddedEvent) {
            MobEffectInstance instance = ((PotionEvent)event).getPotionEffect();
            MobEffect effect = instance.getEffect();
            if (effect instanceof IceBlockEffect) {
                EffectRegistry.handle(handler, instance, entity);
            }
        } else {
            Collection<MobEffectInstance> effects = entity.getActiveEffects();
            effects.forEach(instance -> {
                MobEffect effect = instance.getEffect();
                if (effect instanceof IceBlockEffect) {
                    EffectRegistry.handle(handler, instance, entity);
                }
            });
        }
    }

    private static void syncEffect(LivingEntity entity, MobEffectInstance instance, boolean add) {
        // Only player entities have their potion effects synced.  We need to sync non-player entities, too, so that we
        // are able to render the effect in the overworld.
        if (!(entity instanceof Player)) {
            LivingEntity levelEntity = (LivingEntity)World.getLevelEntity(entity);
            if (levelEntity != null) {
                Minecraft.getInstance().execute(() -> {
                    if (add) levelEntity.forceAddEffect(instance, entity);
                    else levelEntity.removeEffect(instance.getEffect());
                });
            }
        }
    }
}
