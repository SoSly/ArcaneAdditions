/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.compats.BMorph;

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
public class BMorphRegistryEntries {
    public static SpellEffect POLYMORPH;

    @SubscribeEvent
    public static void registerComponents(RegistryEvent.Register<SpellEffect> event) {
        if (ModList.get().isLoaded(CompatModIDs.BMORPH) && !ModList.get().isLoaded(CompatModIDs.IDENTITY)) {
            POLYMORPH = new PolymorphComponent(RLoc.create("components/polymorph"), RLoc.create("textures/spell/component/polymorph.png"));
            event.getRegistry().registerAll(POLYMORPH);
        }
    }
}
