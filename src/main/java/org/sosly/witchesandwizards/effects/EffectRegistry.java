package org.sosly.witchesandwizards.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.sosly.witchesandwizards.WitchesAndWizards;
import org.sosly.witchesandwizards.effects.beneficial.IceBlockEffect;

public class EffectRegistry {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, WitchesAndWizards.MOD_ID);
    public static final RegistryObject<IceBlockEffect> ICE_BLOCK = EFFECTS.register("ice_block", IceBlockEffect::new);
}
