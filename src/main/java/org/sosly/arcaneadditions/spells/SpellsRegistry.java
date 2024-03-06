/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.spells;

import com.mna.Registries;
import com.mna.api.ManaAndArtificeMod;
import com.mna.api.spells.parts.SpellEffect;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import org.sosly.arcaneadditions.compats.CompatModIDs;
import org.sosly.arcaneadditions.spells.components.*;
import org.sosly.arcaneadditions.utils.RLoc;

@Mod.EventBusSubscriber(modid = org.sosly.arcaneadditions.ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpellsRegistry {

    public static SpellEffect ASTRAL_PROJECTION;
    public static SpellEffect COUNTERSPELL;
    public static SpellEffect ENRAGE;
    public static SpellEffect ICE_BLOCK;
    public static SpellEffect LIFE_LINK;
    public static SpellEffect PATH;
    public static SpellEffect PLOW;
    public static SpellEffect POLYMORPH;
    public static SpellEffect STRIP;
    public static SpellEffect TRANSFUSE;
    public static SpellEffect TREE_STRIDE;

    @SubscribeEvent
    public static void registerComponents(RegisterEvent event) {
        if (!ModList.get().isLoaded(ManaAndArtificeMod.ID)) {
            return;
        }

        event.register(((IForgeRegistry) Registries.SpellEffect.get()).getRegistryKey(), (helper) -> {
            ASTRAL_PROJECTION = new AstralProjectionComponent(RLoc.create("textures/spell/component/astral_projection.png"));
            COUNTERSPELL = new CounterspellComponent(RLoc.create("textures/spell/component/counterspell.png"));
            ENRAGE = new EnrageComponent(RLoc.create("textures/spell/component/enrage.png"));
            ICE_BLOCK = new IceBlockComponent(RLoc.create("textures/spell/component/ice_block.png"));
            LIFE_LINK = new LifeLinkComponent(RLoc.create("textures/spell/component/life_link.png"));
            PATH = new PathComponent(RLoc.create("textures/spell/component/path.png"));
            PLOW = new PlowComponent(RLoc.create("textures/spell/component/plow.png"));
            STRIP = new StripComponent(RLoc.create("textures/spell/component/strip.png"));
            TRANSFUSE = new TransfuseComponent(RLoc.create("textures/spell/component/transfuse.png"));
            TREE_STRIDE = new TreeStrideComponent(RLoc.create("textures/spell/component/tree_stride.png"));

            helper.register(RLoc.create("components/astral_projection"), ASTRAL_PROJECTION);
            helper.register(RLoc.create("components/counterspell"), COUNTERSPELL);
            helper.register(RLoc.create("components/enrage"), ENRAGE);
            helper.register(RLoc.create("components/ice_block"), ICE_BLOCK);
            helper.register(RLoc.create("components/life_link"), LIFE_LINK);
            helper.register(RLoc.create("components/path"), PATH);
            helper.register(RLoc.create("components/plow"), PLOW);
            helper.register(RLoc.create("components/strip"), STRIP);
            helper.register(RLoc.create("components/transfuse"), TRANSFUSE);
            helper.register(RLoc.create("components/tree_stride"), TREE_STRIDE);
        });

        // Interop Spells - todo: move this to compats
        if (ModList.get().isLoaded(CompatModIDs.WOODWALKERS)) {
            POLYMORPH = new PolymorphComponent(RLoc.create("textures/spell/component/polymorph.png"));

            event.register(((IForgeRegistry)Registries.SpellEffect.get()).getRegistryKey(), (helper) -> {
                helper.register(RLoc.create("components/polymorph"), SpellsRegistry.POLYMORPH);
            });
        }
    }
}
