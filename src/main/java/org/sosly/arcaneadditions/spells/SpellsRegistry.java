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
    public static SpellEffect ASTRAL_PROJECTION = new AstralProjectionComponent(RLoc.create("components/astral_projection"), RLoc.create("textures/spell/component/astral_projection.png"));
    public static SpellEffect COUNTERSPELL = new CounterspellComponent(RLoc.create("components/counterspell"), RLoc.create("textures/spell/component/counterspell.png"));
    public static SpellEffect ENRAGE = new EnrageComponent(RLoc.create("components/enrage"), RLoc.create("textures/spell/component/enrage.png"));;
    public static SpellEffect ICE_BLOCK = new IceBlockComponent(RLoc.create("components/ice_block"), RLoc.create("textures/spell/component/ice_block.png"));
    public static SpellEffect LIFE_LINK = new LifeLinkComponent(RLoc.create("components/life_link"), RLoc.create("textures/spell/component/life_link.png"));
    public static SpellEffect PATH = new PathComponent(RLoc.create("components/path"), RLoc.create("textures/spell/component/path.png"));;
    public static SpellEffect PLOW = new PlowComponent(RLoc.create("components/plow"), RLoc.create("textures/spell/component/plow.png"));;
    public static SpellEffect STRIP = new StripComponent(RLoc.create("components/strip"), RLoc.create("textures/spell/component/strip.png"));;
    public static SpellEffect TRANSFUSE = new TransfuseComponent(RLoc.create("components/transfuse"), RLoc.create("textures/spell/component/transfuse.png"));;
    public static SpellEffect TREE_STRIDE = new TreeStrideComponent(RLoc.create("components/tree_stride"), RLoc.create("textures/spell/component/tree_stride.png"));;

    @SubscribeEvent
    public static void registerComponents(RegistryEvent.Register<SpellEffect> event) {
        event.getRegistry().register(ASTRAL_PROJECTION);
        event.getRegistry().register(COUNTERSPELL);
        event.getRegistry().register(ENRAGE);
        event.getRegistry().register(ICE_BLOCK);
        event.getRegistry().register(LIFE_LINK);
        event.getRegistry().register(PATH);
        event.getRegistry().register(PLOW);
        event.getRegistry().register(STRIP);
        event.getRegistry().register(TRANSFUSE);
        event.getRegistry().register(TREE_STRIDE);
    }
}
