package org.sosly.arcaneadditions.spells;

import com.mna.api.spells.parts.SpellEffect;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.sosly.arcaneadditions.spells.components.IceBlockComponent;
import org.sosly.arcaneadditions.spells.components.PolymorphComponent;
import org.sosly.arcaneadditions.utils.RLoc;

@Mod.EventBusSubscriber(modid = org.sosly.arcaneadditions.ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpellsRegistry {
    public static final SpellEffect ICE_BLOCK = new IceBlockComponent(RLoc.create("components/ice_block"), RLoc.create("textures/spell/component/ice_block.png"));
    public static final SpellEffect POLYMORPH = new PolymorphComponent(RLoc.create("components/polymorph"), RLoc.create("textures/spell/component/polymorph.png"));

    @SubscribeEvent
    public static void registerComponents(RegistryEvent.Register<SpellEffect> event) {
        event.getRegistry().registerAll(ICE_BLOCK);
        event.getRegistry().registerAll(POLYMORPH);
    }
}
