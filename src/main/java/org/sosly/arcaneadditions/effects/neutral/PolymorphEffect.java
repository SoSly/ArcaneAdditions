package org.sosly.arcaneadditions.effects.neutral;

import com.mna.api.capabilities.resource.ICastingResource;
import com.mna.capabilities.playerdata.magic.PlayerMagicProvider;
import com.mna.items.sorcery.ItemSpell;
import com.mna.spells.crafting.SpellRecipe;
import de.budschie.bmorph.morph.MorphUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.DistExecutor;
import org.sosly.arcaneadditions.capabilities.polymorph.PolymorphProvider;
import org.sosly.arcaneadditions.config.SpellsConfig;
import org.sosly.arcaneadditions.effects.EffectRegistry;
import org.sosly.arcaneadditions.spells.components.PolymorphComponent;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class PolymorphEffect extends MobEffect {
    private final int MANA_COST_FREQUENCY = 20;

    public PolymorphEffect() {
        super(MobEffectCategory.NEUTRAL, 0);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player)) return; // for now, we can't affect non-players

        entity.getCapability(PolymorphProvider.POLYMORPH).ifPresent(polymorph -> {
            float complexity = polymorph.getComplexity();
            WeakReference<Player> caster = polymorph.getCaster(entity.getLevel());

            if (caster == null) {
                entity.removeEffect(EffectRegistry.POLYMORPH.get());
                return;
            }

            Objects.requireNonNull(caster.get()).getCapability(PlayerMagicProvider.MAGIC, null).ifPresent(magic -> {
                ICastingResource resource = magic.getCastingResource();
                if (resource.getAmount() < complexity) {
                    entity.removeEffect(EffectRegistry.POLYMORPH.get());
                } else {
                    resource.consume(complexity);
                }
            });
        });
    }

    public static void handleRestrictedActions(LivingEvent event) {
        runOnEffect(event, (instance, entity) -> {
            if (!(entity instanceof Player)) return; // for now, we can't affect non-players
            Player caster = (Player)entity;

            ItemStack stack = entity.getMainHandItem();

            // If they are holding air or a building block, let them continue.
            if (stack.getItem() instanceof AirItem || stack.getItem() instanceof BlockItem) return;

            // If the item is a spell, check if it's polymorph for ending the effect.
            if (stack.getItem() instanceof ItemSpell) {
                if (SpellsConfig.ALLOW_SPELLCASTING_WHILE_POLYMORPHED.get()) return;

                ItemSpell spellItem = (ItemSpell)stack.getItem();
                SpellRecipe recipe = SpellRecipe.fromNBT(spellItem.getSpellCompound(stack, caster));
                AtomicBoolean isPolymorph = new AtomicBoolean(false);
                recipe.getComponents().stream().forEach(component -> {
                    if (component.getPart() instanceof PolymorphComponent) {
                        isPolymorph.set(true);
                    }
                });
                if (isPolymorph.get() == true) return;
            }

            // Otherwise, cancel the action.
            if (event.isCancelable() && stack != null) event.setCanceled(true);
            if (event instanceof PlayerInteractEvent) event.setResult(Event.Result.DENY);
            if (event instanceof PlayerEvent.HarvestCheck) ((PlayerEvent.HarvestCheck)event).setCanHarvest(false);
        });
    }

    public static void onDamage(LivingDamageEvent event) {
        runOnEffect(event, (instance, entity) -> {
            if (!(entity instanceof Player)) return; // for now, we can't affect non-players

            float amount = event.getAmount();
            if (entity.getHealth() - amount <= 0) {
                event.setCanceled(true);
                entity.removeEffect(EffectRegistry.POLYMORPH.get());
            }
        });
    }

    public static void onDeath(LivingDeathEvent event) {
        runOnEffect(event, (instance, entity) -> {
            if (!(entity instanceof Player)) return; // for now, we can't affect non-players
            event.setCanceled(true);
            entity.removeEffect(EffectRegistry.POLYMORPH.get());
        });
    }

    public static void onEffectRemoved(PotionEvent event) {
        runOnEffect(event, (instance, entity) -> {
            if (!(entity instanceof Player)) return; // for now, we can't affect non-players

            // demorph the target
            if (event.getEntity().getLevel().isClientSide()) {
                MorphUtil.morphToClient(Optional.empty(), Optional.empty(), new ArrayList<>(), (Player) entity);
            } else {
                MorphUtil.morphToServer(Optional.empty(), Optional.empty(), (Player) entity);
            }
            entity.getCapability(PolymorphProvider.POLYMORPH).ifPresent(polymorph -> {
                entity.setHealth(polymorph.getHealth());
                polymorph.reset();
            });
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

    @Override
    public boolean isDurationEffectTick(int durationTicks, int amplifier) {
        return durationTicks % MANA_COST_FREQUENCY == 0;
    }
}
