/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.spells.components;

import com.mna.api.affinity.Affinity;
import com.mna.api.capabilities.Faction;
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
import com.mna.items.ItemInit;
import com.mna.items.sorcery.PhylacteryStaffItem;
import de.budschie.bmorph.morph.MorphManagerHandlers;
import de.budschie.bmorph.morph.MorphUtil;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.sosly.arcaneadditions.capabilities.polymorph.PolymorphProvider;
import org.sosly.arcaneadditions.compats.BMorph.BMorphRegistryEntries;
import org.sosly.arcaneadditions.configs.Config;
import org.sosly.arcaneadditions.networking.PacketHandler;
import org.sosly.arcaneadditions.networking.messages.clientbound.SyncPolymorphCapabilitiesToClient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PolymorphComponent extends SpellEffect {
    private List<SpellReagent> reagents = new ArrayList<SpellReagent>();

    public PolymorphComponent(ResourceLocation registryName, ResourceLocation guiIcon) {
        super(registryName, guiIcon, new AttributeValuePair(Attribute.MAGNITUDE, 1.0F, 1.0F, 4.0F, 1.0F, 25.0F));
        this.reagents.add(new SpellReagent(new ItemStack(ItemInit.ANIMUS_DUST.get()), false, false, true));
    }

    @Override
    public ComponentApplicationResult ApplyEffect(SpellSource caster, SpellTarget target, IModifiedSpellPart<SpellEffect> iModifiedSpellPart, SpellContext spellContext) {
        if (!caster.isPlayerCaster() || target.getLivingEntity() == null || !(target.getEntity() instanceof Player)) {
            return ComponentApplicationResult.FAIL;
        }

        LivingEntity targetEntity = target.getLivingEntity();

        // Demorph
        if (targetEntity.hasEffect(BMorphRegistryEntries.POLYMORPH_EFFECT)) {
            targetEntity.getCapability(PolymorphProvider.POLYMORPH).ifPresent(polymorph -> {
                        polymorph.setMorphing(true);
            });
            targetEntity.removeEffect(BMorphRegistryEntries.POLYMORPH_EFFECT);
            return ComponentApplicationResult.SUCCESS;
        }

        Level level = targetEntity.getLevel();
        if (!level.isClientSide()) {
            ItemStack phylactery = caster.getHand() == InteractionHand.MAIN_HAND ? targetEntity.getOffhandItem() : targetEntity.getMainHandItem();
            ServerPlayer casterPlayer = (ServerPlayer)Objects.requireNonNull(caster.getCaster());

            if (!PhylacteryStaffItem.isFilled(phylactery)) {
                casterPlayer.sendMessage(new TranslatableComponent("arcaneadditions:components/polymorph.nonphylactery"), Util.NIL_UUID);
                return ComponentApplicationResult.NOT_PRESENT;
            }

            EntityType<? extends Mob> type = PhylacteryStaffItem.getEntityType(phylactery);
            if (type == null) {
                casterPlayer.sendMessage(new TranslatableComponent("arcaneadditions:components/polymorph.nonphylactery"), Util.NIL_UUID);
                return ComponentApplicationResult.NOT_PRESENT;
            } else if (!isFormAllowed(type, iModifiedSpellPart)) {
                casterPlayer.sendMessage(new TranslatableComponent("arcaneadditions:components/polymorph.notallowed"), Util.NIL_UUID);
                return ComponentApplicationResult.FAIL;
            }

            targetEntity.getCapability(PolymorphProvider.POLYMORPH).ifPresent(polymorph -> {
                polymorph.setMorphing(true);
                polymorph.setCaster(caster.getPlayer());
                polymorph.setComplexity(spellContext.getSpell().getComplexity());
                polymorph.setHealth(targetEntity.getHealth());
                PacketHandler.network.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)targetEntity), new SyncPolymorphCapabilitiesToClient(polymorph));
            });

            CompoundTag nbt = new CompoundTag();
            MorphUtil.morphToServer(Optional.of(MorphManagerHandlers.FALLBACK.createMorph(ForgeRegistries.ENTITIES.getValue(type.getRegistryName()), nbt, null, true)), Optional.empty(), (ServerPlayer)target.getEntity());
            MobEffectInstance instance = new MobEffectInstance(BMorphRegistryEntries.POLYMORPH_EFFECT, Integer.MAX_VALUE);
            instance.setNoCounter(true);
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
        Config.SERVER.polymorph.tiers.get().forEach(tierList -> {
            tier.getAndIncrement();

            if (tierList.contains(Objects.requireNonNull(type.getRegistryName()).toString())) {
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
        return 25.0f;
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
    public List<SpellReagent> getRequiredReagents(@Nullable Player caster) {
        if (caster == null) {
            return this.reagents;
        } else {
            boolean isMorphed = caster.hasEffect(BMorphRegistryEntries.POLYMORPH_EFFECT);

            MutableBoolean isFey = new MutableBoolean(false);
            caster.getCapability(PlayerProgressionProvider.PROGRESSION).ifPresent((p) -> {
                isFey.setValue(p.getAlliedFaction() == Faction.FEY_COURT);
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
    }
}
