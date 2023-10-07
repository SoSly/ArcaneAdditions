package org.sosly.arcaneadditions.compats.Identity;

import com.mna.api.spells.parts.SpellEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.sosly.arcaneadditions.compats.CompatModIDs;
import org.sosly.arcaneadditions.effects.beneficial.PolymorphEffect;
import org.sosly.arcaneadditions.spells.components.PolymorphComponent;
import org.sosly.arcaneadditions.utils.RLoc;

@Mod.EventBusSubscriber(modid = org.sosly.arcaneadditions.ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IdentityRegistryEntries {
    public static SpellEffect POLYMORPH;

    @SubscribeEvent
    public static void registerComponents(RegistryEvent.Register<SpellEffect> event) {
        if (ModList.get().isLoaded(CompatModIDs.IDENTITY)) {
            POLYMORPH = new PolymorphComponent(RLoc.create("components/polymorph"), RLoc.create("textures/spell/component/polymorph.png"));
            event.getRegistry().registerAll(POLYMORPH);
        }
    }
}
