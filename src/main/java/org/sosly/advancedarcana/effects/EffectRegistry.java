package org.sosly.advancedarcana.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.sosly.advancedarcana.AdvancedArcana;
import org.sosly.advancedarcana.effects.beneficial.IceBlockEffect;
import org.sosly.advancedarcana.effects.neutral.IceBlockExhaustionEffect;
import org.sosly.advancedarcana.effects.neutral.PolymorphEffect;

public class EffectRegistry {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, AdvancedArcana.MOD_ID);
    public static final RegistryObject<IceBlockEffect> ICE_BLOCK = EFFECTS.register("ice_block", IceBlockEffect::new);
    public static final RegistryObject<IceBlockExhaustionEffect> ICE_BLOCK_EXHAUSTION = EFFECTS.register("ice_block-exhaustion", IceBlockExhaustionEffect::new);
    public static final RegistryObject<PolymorphEffect> POLYMORPH = EFFECTS.register("polymorph", PolymorphEffect::new);

    @FunctionalInterface
    public interface ILivingMobEffectInstanceHandler {
        void handle(MobEffectInstance inst, LivingEntity entity);
    }

    public static void handle(ILivingMobEffectInstanceHandler handler, MobEffectInstance inst, LivingEntity entity) {
        handler.handle(inst, entity);
    }
}
