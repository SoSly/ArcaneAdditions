package org.sosly.witchesandwizards.spells.components;

import com.google.common.collect.ImmutableList;
import com.mna.api.affinity.Affinity;
import com.mna.api.sound.SFX;
import com.mna.api.spells.ComponentApplicationResult;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.sosly.witchesandwizards.effects.EffectRegistry;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public class PolymorphComponent extends SpellEffect {
    private final ImmutableList<MobCategory> ALLOWED_CATEGORIES = ImmutableList.of(MobCategory.CREATURE, MobCategory.WATER_CREATURE, MobCategory.AMBIENT, MobCategory.WATER_AMBIENT);

    public PolymorphComponent(ResourceLocation registryName, ResourceLocation guiIcon) {
        super(registryName, guiIcon);
    }

    @Override
    public ComponentApplicationResult ApplyEffect(SpellSource source, SpellTarget target, IModifiedSpellPart<SpellEffect> iModifiedSpellPart, SpellContext spellContext) {
        if (!source.isPlayerCaster() || !(target.getEntity() instanceof Player) || source.getCaster() != target.getLivingEntity()) {
            return ComponentApplicationResult.FAIL;
        }

        // Demorph
        if (target.isLivingEntity() && target.getLivingEntity().getEffect(EffectRegistry.POLYMORPH.get()) != null) {
            target.getLivingEntity().removeEffect(EffectRegistry.POLYMORPH.get());
            return ComponentApplicationResult.SUCCESS;
        }

        Level level = target.getLivingEntity().getLevel();
        if (!level.isClientSide()) {
            ItemStack phylactery = source.getHand() == InteractionHand.MAIN_HAND ? target.getLivingEntity().getOffhandItem() : target.getLivingEntity().getMainHandItem();

            if (!PhylacteryStaffItem.isFilled(phylactery)) {
                source.getCaster().sendMessage(new TranslatableComponent("wnw:components/polymorph.nonphylactery"), Util.NIL_UUID);
                return ComponentApplicationResult.NOT_PRESENT;
            }

            EntityType<? extends Mob> type = PhylacteryStaffItem.getEntityType(phylactery);
            if (type == null) {
                source.getCaster().sendMessage(new TranslatableComponent("wnw:components/polymorph.nonphylactery"), Util.NIL_UUID);
                return ComponentApplicationResult.NOT_PRESENT;
            } else if (!ALLOWED_CATEGORIES.contains(type.getCategory())) {
                source.getCaster().sendMessage(new TranslatableComponent("wnw:components/polymorph.notallowed"), Util.NIL_UUID);
                return ComponentApplicationResult.FAIL;
            }

            CompoundTag nbt = new CompoundTag();
            MorphUtil.morphToServer(Optional.of(MorphManagerHandlers.FALLBACK.createMorph(ForgeRegistries.ENTITIES.getValue(type.getRegistryName()), nbt, null, true)), Optional.empty(), (ServerPlayer)target.getEntity());
            MobEffectInstance instance = new MobEffectInstance(EffectRegistry.POLYMORPH.get(), Integer.MAX_VALUE);
            instance.setNoCounter(true);
            target.getLivingEntity().addEffect(instance);
            return ComponentApplicationResult.SUCCESS;
        }
        return ComponentApplicationResult.FAIL;
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
        return 50;
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
