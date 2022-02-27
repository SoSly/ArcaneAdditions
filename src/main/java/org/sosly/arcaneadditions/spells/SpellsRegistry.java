/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.spells;

import com.mna.api.spells.parts.SpellEffect;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.sosly.arcaneadditions.spells.components.*;
import org.sosly.arcaneadditions.utils.RLoc;

@Mod.EventBusSubscriber(modid = org.sosly.arcaneadditions.ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpellsRegistry {
    public static final SpellEffect ICE_BLOCK = new IceBlockComponent(RLoc.create("components/ice_block"), RLoc.create("textures/spell/component/ice_block.png"));
    public static final SpellEffect PATH = new PathComponent(RLoc.create("components/path"), RLoc.create("textures/spell/component/path.png"));
    public static final SpellEffect PLOW = new PlowComponent(RLoc.create("components/plow"), RLoc.create("textures/spell/component/plow.png"));
    public static final SpellEffect POLYMORPH = new PolymorphComponent(RLoc.create("components/polymorph"), RLoc.create("textures/spell/component/polymorph.png"));
    public static final SpellEffect STRIP = new StripComponent(RLoc.create("components/strip"), RLoc.create("textures/spell/component/strip.png"));
    public static final SpellEffect TREE_STRIDE = new TreeStrideComponent(RLoc.create("components/tree_stride"), RLoc.create("textures/spell/component/tree_stride.png"));

    @SubscribeEvent
    public static void registerComponents(RegistryEvent.Register<SpellEffect> event) {
        event.getRegistry().registerAll(ICE_BLOCK);
        event.getRegistry().registerAll(PATH);
        event.getRegistry().registerAll(PLOW);
        event.getRegistry().registerAll(POLYMORPH);
        event.getRegistry().registerAll(STRIP);
        event.getRegistry().registerAll(TREE_STRIDE);
    }
}
