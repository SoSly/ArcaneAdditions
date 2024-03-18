package org.sosly.arcaneadditions.rituals;

import com.mna.Registries;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.rituals.effects.BindFamiliarRitual;
import org.sosly.arcaneadditions.utils.RLoc;

@Mod.EventBusSubscriber(modid = ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RitualRegistry {
    @SubscribeEvent
    public static void registerRitualEffects(RegisterEvent event) {
        event.register(Registries.RitualEffect.get().getRegistryKey(), helper -> {
            helper.register(RLoc.create("ritual/bind_familiar"), new BindFamiliarRitual(RLoc.create("rituals/bind_familiar")));
        });
    }
}
