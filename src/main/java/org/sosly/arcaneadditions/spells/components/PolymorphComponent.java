/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.spells.components;

import com.mna.api.affinity.Affinity;
import com.mna.api.sound.SFX;
import com.mna.api.spells.ComponentApplicationResult;
import com.mna.api.spells.attributes.*;
import com.mna.api.spells.base.IModifiedSpellPart;
import com.mna.api.spells.parts.SpellEffect;
import com.mna.api.spells.targeting.SpellContext;
import com.mna.api.spells.targeting.SpellSource;
import com.mna.api.spells.targeting.SpellTarget;
import com.mna.items.sorcery.PhylacteryStaffItem;
import de.budschie.bmorph.morph.MorphManagerHandlers;
import de.budschie.bmorph.morph.MorphUtil;
import net.minecraft.Util;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.sosly.arcaneadditions.capabilities.polymorph.PolymorphProvider;
import org.sosly.arcaneadditions.config.*;
import org.sosly.arcaneadditions.effects.EffectRegistry;
import org.sosly.arcaneadditions.networking.PacketHandler;
import org.sosly.arcaneadditions.networking.messages.clientbound.SyncPolymorphCapabilitiesToClient;

import java.util.*;
import java.util.concurrent.atomic.*;

public class PolymorphComponent extends SpellEffect {

    public PolymorphComponent(ResourceLocation registryName, ResourceLocation guiIcon) {
        super(registryName, guiIcon, new AttributeValuePair(Attribute.MAGNITUDE, 1.0F, 1.0F, 4.0F, 1.0F, 25.0F));
    }

    @Override
    public ComponentApplicationResult ApplyEffect(SpellSource caster, SpellTarget target, IModifiedSpellPart<SpellEffect> iModifiedSpellPart, SpellContext spellContext) {
        if (!caster.isPlayerCaster() || target.getLivingEntity() == null || !(target.getEntity() instanceof Player)) {
            return ComponentApplicationResult.FAIL;
        }

        LivingEntity targetEntity = target.getLivingEntity();

        // Demorph
        if (targetEntity.getEffect(EffectRegistry.POLYMORPH.get()) != null) {
            targetEntity.removeEffect(EffectRegistry.POLYMORPH.get());
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
                polymorph.setCaster(caster.getPlayer());
                polymorph.setComplexity(spellContext.getSpell().getComplexity());
                polymorph.setHealth(targetEntity.getHealth());
                PacketHandler.network.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)targetEntity), new SyncPolymorphCapabilitiesToClient(polymorph));
            });

            CompoundTag nbt = new CompoundTag();
            MorphUtil.morphToServer(Optional.of(MorphManagerHandlers.FALLBACK.createMorph(ForgeRegistries.ENTITIES.getValue(type.getRegistryName()), nbt, null, true)), Optional.empty(), (ServerPlayer)target.getEntity());
            MobEffectInstance instance = new MobEffectInstance(EffectRegistry.POLYMORPH.get(), Integer.MAX_VALUE);
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
        ServerConfig.POLYMORPH_TIERS.get().forEach(tierList -> {
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
        return 50.0f;
    }

    @Override
    public int requiredXPForRote() {
        return 500;
    }

    @Override
    public SoundEvent SoundEffect() {
        return SFX.Spell.Cast.WATER;
    }
}
