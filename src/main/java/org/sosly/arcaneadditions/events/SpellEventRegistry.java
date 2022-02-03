package org.sosly.arcaneadditions.events;

import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.*;
import org.sosly.arcaneadditions.*;
import org.sosly.arcaneadditions.capabilities.polymorph.IPolymorphCapability;
import org.sosly.arcaneadditions.capabilities.polymorph.PolymorphProvider;
import org.sosly.arcaneadditions.effects.beneficial.IceBlockEffect;
import org.sosly.arcaneadditions.effects.neutral.PolymorphEffect;

@Mod.EventBusSubscriber(modid = ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpellEventRegistry {
    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<?> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(IPolymorphCapability.POLYMORPH_CAPABILITY, new PolymorphProvider());
        }
    }

    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        IceBlockEffect.handleRestrictedActions(event);
        PolymorphEffect.handleRestrictedActions(event);
    }

    @SubscribeEvent
    public static void onBreakingBlock(PlayerEvent.BreakSpeed event) {
        IceBlockEffect.handleRestrictedActions(event);
        PolymorphEffect.handleRestrictedActions(event);
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        IceBlockEffect.handleDamageEvents(event);
        PolymorphEffect.onDamage(event);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        IceBlockEffect.handleRestrictedActions(event);
        PolymorphEffect.handleRestrictedActions(event);
    }

    @SubscribeEvent
    public static void onHarvest(PlayerEvent.HarvestCheck event) {
        IceBlockEffect.handleRestrictedActions(event);
        PolymorphEffect.handleRestrictedActions(event);
    }

    @SubscribeEvent
    public static void onItemInteract(PlayerInteractEvent.RightClickItem event) {
        IceBlockEffect.handleRestrictedActions(event);
        PolymorphEffect.handleRestrictedActions(event);
    }

    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        IceBlockEffect.handleRestrictedActions(event);
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        IceBlockEffect.handleRestrictedActions(event);
    }

    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        IceBlockEffect.handleRestrictedActions(event);
    }

    @SubscribeEvent
    public static void onPickup(PlayerEvent.ItemPickupEvent event) {
        IceBlockEffect.handleRestrictedActions(event);
    }

    @SubscribeEvent
    public static void onPotionAdded(PotionEvent.PotionAddedEvent event) {
        IceBlockEffect.onEffectAdded(event);
    }

    @SubscribeEvent
    public static void onPotionExpired(PotionEvent.PotionExpiryEvent event) {
        IceBlockEffect.onEffectRemoved(event);
    }

    @SubscribeEvent
    public static void onPotionRemoved(PotionEvent.PotionRemoveEvent event) {
        IceBlockEffect.onEffectRemoved(event);
        PolymorphEffect.onEffectRemoved(event);
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        IceBlockEffect.handleRestrictedActions(event);
        PolymorphEffect.handleRestrictedActions(event);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static <E extends LivingEntity, M extends EntityModel<E>> void onPostRenderLiving(RenderLivingEvent.Post<E, M> event) {
        IceBlockEffect.handleRenderEvent(event);
    }
}
