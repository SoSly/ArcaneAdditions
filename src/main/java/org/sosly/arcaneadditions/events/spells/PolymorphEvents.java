/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.events.spells;

import com.mna.api.ManaAndArtificeMod;
import com.mna.api.spells.ICanContainSpell;
import com.mna.api.spells.base.ISpellDefinition;
import de.budschie.bmorph.morph.MorphUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.capabilities.polymorph.IPolymorphCapability;
import org.sosly.arcaneadditions.capabilities.polymorph.PolymorphProvider;
import org.sosly.arcaneadditions.compats.BMorph.BMorphRegistryEntries;
import org.sosly.arcaneadditions.compats.CompatModIDs;
import org.sosly.arcaneadditions.configs.ServerConfig;
import org.sosly.arcaneadditions.effects.EffectRegistry;
import org.sosly.arcaneadditions.effects.beneficial.PolymorphEffect;
import org.sosly.arcaneadditions.spells.components.PolymorphComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber(modid = ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PolymorphEvents {
    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<?> event) {
        if (event.getObject() instanceof Player && ModList.get().isLoaded(CompatModIDs.BMORPH)) {
            event.addCapability(IPolymorphCapability.POLYMORPH_CAPABILITY, new PolymorphProvider());
        }
    }

    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        handleRestrictedActions(event);
    }

    @SubscribeEvent
    public static void onBreakingBlock(PlayerEvent.BreakSpeed event) {
        handleRestrictedActions(event);
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        runOnEffect(event, (instance, entity) -> {
            if (!(entity instanceof Player)) return; // for now, we can't affect non-players

            float amount = event.getAmount();
            if (entity.getHealth() - amount <= 0) {
                event.setCanceled(true);
                entity.removeEffect(BMorphRegistryEntries.POLYMORPH_EFFECT);
            }
        });
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        handleRestrictedActions(event);
    }

    @SubscribeEvent
    public static void onHarvest(PlayerEvent.HarvestCheck event) {
        handleRestrictedActions(event);
    }

    @SubscribeEvent
    public static void onItemInteract(PlayerInteractEvent.RightClickItem event) {
        handleRestrictedActions(event);
    }

    @SubscribeEvent
    public static void onPotionRemoved(PotionEvent.PotionRemoveEvent event) {
        runOnEffect(event, (instance, entity) -> {
            if (!(entity instanceof Player)) return; // for now, we can't affect non-players

            // demorph the target
            if (event.getEntity().getLevel().isClientSide()) {
                MorphUtil.morphToClient(Optional.empty(), Optional.empty(), new ArrayList<>(), (Player) entity);
            } else {
                MorphUtil.morphToServer(Optional.empty(), Optional.empty(), (Player) entity);

                entity.getCapability(PolymorphProvider.POLYMORPH).ifPresent(polymorph -> {
                    entity.setHealth(polymorph.getHealth());
                    polymorph.reset();
                });
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        handleRestrictedActions(event);
    }

    private static void handleRestrictedActions(LivingEvent event) {
        runOnEffect(event, (instance, entity) -> {
            if (!(entity instanceof Player caster)) return; // for now, we can't affect non-players

            ItemStack stack = entity.getMainHandItem();

            // If they are holding air or a building block, let them continue.
            if (stack.getItem() instanceof AirItem || stack.getItem() instanceof BlockItem) return;

            // If the item is a spell, check if it's polymorph for ending the effect.
            if (stack.getItem() instanceof ICanContainSpell) {
                if (ServerConfig.ALLOW_SPELLCASTING_WHILE_POLYMORPHED.get()) return;

                ISpellDefinition recipe = ManaAndArtificeMod.getSpellHelper().parseSpellDefinition(stack);
                AtomicBoolean isPolymorph = new AtomicBoolean(false);
                recipe.getComponents().forEach(component -> {
                    if (component.getPart() instanceof PolymorphComponent) {
                        isPolymorph.set(true); // todo: We should probably also make sure that the spell is going to affect the target in question.
                    }
                });
                if (isPolymorph.get()) return;
            }

            // Otherwise, cancel the action.
            if (event.isCancelable()) event.setCanceled(true);
            if (event instanceof PlayerInteractEvent) event.setResult(Event.Result.DENY);
            if (event instanceof PlayerEvent.HarvestCheck) ((PlayerEvent.HarvestCheck)event).setCanHarvest(false);
        });
    }

    private static void runOnEffect(Event event, EffectRegistry.ILivingMobEffectInstanceHandler handler) {
        LivingEntity entity;

        if (event instanceof LivingEvent) {
            entity = (LivingEntity)((LivingEvent)event).getEntity();
        } else {
            return; // not sure how we got here but let's bail out just in case.
        }

        Collection<MobEffectInstance> effects = entity.getActiveEffects();
        for (MobEffectInstance instance : effects) {
            MobEffect effect = instance.getEffect();
            if (effect instanceof PolymorphEffect) {
                EffectRegistry.handle(handler, instance, entity);
            }
        }
    }
}
