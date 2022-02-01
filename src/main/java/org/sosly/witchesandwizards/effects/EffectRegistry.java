package org.sosly.witchesandwizards.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.sosly.witchesandwizards.WitchesAndWizards;
import org.sosly.witchesandwizards.effects.beneficial.IceBlockEffect;
import org.sosly.witchesandwizards.effects.neutral.IceBlockExhaustionEffect;

public class EffectRegistry {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, WitchesAndWizards.MOD_ID);
    public static final RegistryObject<IceBlockEffect> ICE_BLOCK = EFFECTS.register("ice_block", IceBlockEffect::new);
    public static final RegistryObject<IceBlockExhaustionEffect> ICE_BLOCK_EXHAUSTION = EFFECTS.register("ice_block-exhaustion", IceBlockExhaustionEffect::new);

    @FunctionalInterface
    public interface ILivingMobEffectInstanceHandler {
        void handle(MobEffectInstance inst, LivingEntity entity);
    }

    public static void handle(ILivingMobEffectInstanceHandler handler, MobEffectInstance inst, LivingEntity entity) {
        handler.handle(inst, entity);
    }
}
