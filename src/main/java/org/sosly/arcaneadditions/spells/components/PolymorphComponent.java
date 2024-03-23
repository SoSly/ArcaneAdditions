/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.spells.components;

import com.mna.api.affinity.Affinity;
import com.mna.api.capabilities.IPlayerProgression;
import com.mna.api.config.GeneralConfigValues;
import com.mna.api.sound.SFX;
import com.mna.api.spells.ComponentApplicationResult;
import com.mna.api.spells.SpellReagent;
import com.mna.api.spells.attributes.Attribute;
import com.mna.api.spells.attributes.AttributeValuePair;
import com.mna.api.spells.base.IModifiedSpellPart;
import com.mna.api.spells.parts.SpellEffect;
import com.mna.api.spells.targeting.SpellContext;
import com.mna.api.spells.targeting.SpellSource;
import com.mna.api.spells.targeting.SpellTarget;
import com.mna.capabilities.playerdata.progression.PlayerProgressionProvider;
import com.mna.factions.Factions;
import com.mna.items.ItemInit;
import com.mna.items.sorcery.PhylacteryStaffItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.sosly.arcaneadditions.api.spells.components.IPolymorphProvider;
import org.sosly.arcaneadditions.capabilities.polymorph.PolymorphProvider;
import org.sosly.arcaneadditions.compats.CompatRegistry;
import org.sosly.arcaneadditions.configs.Config;
import org.sosly.arcaneadditions.effects.EffectRegistry;
import org.sosly.arcaneadditions.networking.PacketHandler;
import org.sosly.arcaneadditions.networking.messages.clientbound.SyncPolymorphCapabilitiesToClient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PolymorphComponent extends SpellEffect {
    private final List<SpellReagent> reagents = new ArrayList<SpellReagent>();

    public PolymorphComponent(ResourceLocation guiIcon) {
        super(guiIcon, new AttributeValuePair(Attribute.MAGNITUDE, 1.0F, 1.0F, 4.0F, 1.0F, 1.0F));
    }

    private boolean initReagents() {
        if (!ItemInit.ANIMUS_DUST.isPresent()) {
            return false;
        }
        if (reagents.isEmpty()) {
            reagents.add(new SpellReagent(this, new ItemStack(ItemInit.ANIMUS_DUST.get()), false, false, true));
        }
        return true;
    }

    @Override
    public ComponentApplicationResult ApplyEffect(SpellSource caster, SpellTarget target, IModifiedSpellPart<SpellEffect> iModifiedSpellPart, SpellContext spellContext) {
        if (!caster.isPlayerCaster() || target.getLivingEntity() == null || !(target.getEntity() instanceof Player)) {
            return ComponentApplicationResult.FAIL;
        }

        // get the polymorpher from the compat registry
        IPolymorphProvider polymorpher = CompatRegistry.getPolymorphCompat();
        if (polymorpher == null) {
            return ComponentApplicationResult.FAIL;
        }

        LivingEntity targetEntity = target.getLivingEntity();

        // if the target is already polymorphed, then we are removing that effect
        if (targetEntity.hasEffect(EffectRegistry.POLYMORPH.get())) {
            targetEntity.getCapability(PolymorphProvider.POLYMORPH).ifPresent(polymorph -> {
                        polymorph.setMorphing(true);
            });
            targetEntity.removeEffect(EffectRegistry.POLYMORPH.get());
            return ComponentApplicationResult.SUCCESS;
        }

        Level level = targetEntity.level();
        if (!level.isClientSide()) {
            // get the phylactery from the target
            ItemStack phylactery = caster.getHand() == InteractionHand.MAIN_HAND ? targetEntity.getOffhandItem() : targetEntity.getMainHandItem();
            ServerPlayer casterPlayer = (ServerPlayer)Objects.requireNonNull(caster.getCaster());

            if (!PhylacteryStaffItem.isFilled(phylactery)) {
                casterPlayer.sendSystemMessage(Component.translatable("arcaneadditions:components/polymorph.nonphylactery"));
                return ComponentApplicationResult.NOT_PRESENT;
            }

            EntityType<? extends Mob> type = PhylacteryStaffItem.getEntityType(phylactery);
            if (type == null) {
                casterPlayer.sendSystemMessage(Component.translatable("arcaneadditions:components/polymorph.nonphylactery"));
                return ComponentApplicationResult.NOT_PRESENT;
            } else if (!isFormAllowed(type, iModifiedSpellPart)) {
                casterPlayer.sendSystemMessage(Component.translatable("arcaneadditions:components/polymorph.notallowed"));
                return ComponentApplicationResult.FAIL;
            }

            // configure the polymorph capabilities for the target
            targetEntity.getCapability(PolymorphProvider.POLYMORPH).ifPresent(polymorph -> {
                polymorph.setMorphing(true);
                polymorph.setCaster(caster.getPlayer());
                polymorph.setComplexity(spellContext.getSpell().getComplexity());
                polymorph.setHealth(targetEntity.getHealth());
                PacketHandler.network.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)targetEntity), new SyncPolymorphCapabilitiesToClient(polymorph));
            });

            // transform the target into the creature
            polymorpher.polymorph(casterPlayer, type.create(level));

            // remove bonus health from tiers
            removeBonusHealth(casterPlayer);

            // apply the polymorph effect
            MobEffectInstance instance = new MobEffectInstance(EffectRegistry.POLYMORPH.get(), Integer.MAX_VALUE);
            targetEntity.addEffect(instance);
            targetEntity.setHealth(targetEntity.getMaxHealth());

            return ComponentApplicationResult.SUCCESS;
        }
        return ComponentApplicationResult.FAIL;
    }

    private boolean isFormAllowed(EntityType<? extends Mob> type, IModifiedSpellPart<SpellEffect> iModifiedSpellPart) {
        AtomicBoolean allowed = new AtomicBoolean(false);
        AtomicInteger tier = new AtomicInteger();
        AtomicReference<Float> magnitude = new AtomicReference<>(iModifiedSpellPart.getValue(Attribute.MAGNITUDE));

        String resourceLocation = ForgeRegistries.ENTITY_TYPES.getResourceKey(type).get().location().toString();

        Config.SERVER.polymorph.tiers.get().forEach(tierList -> {
            tier.getAndIncrement();

            if (tierList.contains(resourceLocation)) {
                if (magnitude.get() >= tier.get()) {
                    allowed.set(true);
                }
            }
        });
        return allowed.get();
    }

    @Override
    public boolean canBeChanneled() {
        return false;
    }

    @Override
    public Affinity getAffinity() {
        return Affinity.WATER;
    }

    @Override
    public float initialComplexity() {
        return 1.0f;
    }

    @Override
    public int requiredXPForRote() {
        return 500;
    }

    @Override
    public SoundEvent SoundEffect() {
        return SFX.Spell.Cast.WATER;
    }

    @Override
    public List<SpellReagent> getRequiredReagents(@Nullable Player caster, @Nullable InteractionHand hand) {
        if (!initReagents()) {
            return null;
        }

        if (caster == null) {
            return this.reagents;
        }

        boolean isMorphed = caster.hasEffect(EffectRegistry.POLYMORPH.get());

        MutableBoolean isFey = new MutableBoolean(false);
        caster.getCapability(PlayerProgressionProvider.PROGRESSION).ifPresent((p) -> {
            isFey.setValue(p.getAlliedFaction() == Factions.FEY);
        });

        MutableBoolean isMorphing = new MutableBoolean(false);
        caster.getCapability(PolymorphProvider.POLYMORPH).ifPresent((p) -> {
            isMorphing.setValue(p.isMorphing());
        });

        if (isFey.booleanValue()) {
            // Fey don't pay
            return null;
        }

        if (!isMorphed && !isMorphing.getValue()) {
            // about to morph
            return this.reagents;
        }

        if (isMorphed && isMorphing.getValue()) {
            // just morphed
            caster.getCapability(PolymorphProvider.POLYMORPH).ifPresent((p) -> {
                p.setMorphing(false);
            });
            return this.reagents;
        }

        if (!isMorphed && isMorphing.getValue()) {
            // just de-morphed
            caster.getCapability(PolymorphProvider.POLYMORPH).ifPresent((p) -> {
                p.setMorphing(false);
            });
            return null;
        }

        if (isMorphed && !isMorphing.getValue()) {
            // about to de-morph
            return null;
        }

        return this.reagents;
    }

    public static void resetBonusHealth(ServerPlayer target) {
        int roteTier;
        int index;
        int modifierAmount;
        AttributeModifier modifier;
        AttributeInstance inst = target.getAttribute(Attributes.MAX_HEALTH);

        if (inst == null) {
            return;
        }

        AtomicInteger tier = new AtomicInteger();
        target.getCapability(PlayerProgressionProvider.PROGRESSION).ifPresent(progression -> {
            tier.set(progression.getTier());
        });

        if (tier.get() == 0) {
            return;
        }

        removeBonusHealth(target);

        for(roteTier = 1; roteTier <= 5; ++roteTier) {
            index = roteTier - 1;
            int boost = GeneralConfigValues.TierHealthBoosts.get(index);
            modifierAmount = boost >= index ? boost : 0;
            modifier = new AttributeModifier(UUID.fromString(IPlayerProgression.Tier_Health_Boost_IDs[index]), "Tier Health Boost " + roteTier, modifierAmount, AttributeModifier.Operation.ADDITION);
            if (modifierAmount > 0 && tier.get() >= roteTier && !inst.hasModifier(modifier)) {
                inst.addPermanentModifier(modifier);
            }
        }
    }

    public static void removeBonusHealth(ServerPlayer target) {
        int roteTier;
        int index;
        int modifierAmount;
        AttributeModifier modifier;
        AttributeInstance inst = target.getAttribute(Attributes.MAX_HEALTH);

        if (inst == null) {
            return;
        }

        for(roteTier = 1; roteTier <= 5; ++roteTier) {
            index = roteTier - 1;
            int boost = GeneralConfigValues.TierHealthBoosts.get(index);
            modifierAmount = boost >= index ? boost : 0;
            modifier = new AttributeModifier(UUID.fromString(IPlayerProgression.Tier_Health_Boost_IDs[index]), "Tier Health Boost " + roteTier, modifierAmount, AttributeModifier.Operation.ADDITION);
            if (inst.hasModifier(modifier)) {
                inst.removeModifier(modifier.getId());
            }
        }
    }
}
