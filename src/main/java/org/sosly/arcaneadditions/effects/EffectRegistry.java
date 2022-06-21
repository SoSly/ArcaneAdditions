/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.sosly.arcaneadditions.effects.beneficial.IceBlockEffect;
import org.sosly.arcaneadditions.effects.beneficial.LifeLinkEffect;
import org.sosly.arcaneadditions.effects.harmful.DoomedEffect;
import org.sosly.arcaneadditions.effects.harmful.EnragedEffect;
import org.sosly.arcaneadditions.effects.neutral.IceBlockExhaustionEffect;

public class EffectRegistry {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, org.sosly.arcaneadditions.ArcaneAdditions.MOD_ID);
    public static final RegistryObject<DoomedEffect> DOOMED = EFFECTS.register("doomed", DoomedEffect::new);
    public static final RegistryObject<EnragedEffect> ENRAGED = EFFECTS.register("enraged", EnragedEffect::new);
    public static final RegistryObject<IceBlockEffect> ICE_BLOCK = EFFECTS.register("ice_block", IceBlockEffect::new);
    public static final RegistryObject<IceBlockExhaustionEffect> ICE_BLOCK_EXHAUSTION = EFFECTS.register("ice_block-exhaustion", IceBlockExhaustionEffect::new);
    public static final RegistryObject<LifeLinkEffect> LIFE_LINK = EFFECTS.register("life_link", LifeLinkEffect::new);

    @FunctionalInterface
    public interface ILivingMobEffectInstanceHandler {
        void handle(MobEffectInstance inst, LivingEntity entity);
    }

    public static void handle(ILivingMobEffectInstanceHandler handler, MobEffectInstance inst, LivingEntity entity) {
        handler.handle(inst, entity);
    }
}
