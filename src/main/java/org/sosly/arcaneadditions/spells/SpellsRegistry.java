/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.spells;

import com.mna.Registries;
import com.mna.api.ManaAndArtificeMod;
import com.mna.api.spells.parts.Shape;
import com.mna.api.spells.parts.SpellEffect;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import org.sosly.arcaneadditions.compats.CompatModIDs;
import org.sosly.arcaneadditions.spells.components.*;
import org.sosly.arcaneadditions.spells.shapes.FamiliarShape;
import org.sosly.arcaneadditions.spells.shapes.SharedShape;
import org.sosly.arcaneadditions.utils.RLoc;

@Mod.EventBusSubscriber(modid = org.sosly.arcaneadditions.ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpellsRegistry {
    public static final Shape FAMILIAR = new FamiliarShape(RLoc.create("textures/spell/shape/familiar.png"));
    public static final Shape SHARED = new SharedShape(RLoc.create("textures/spell/shape/shared.png"));
    public static final SpellEffect ASTRAL_PROJECTION = new AstralProjectionComponent(RLoc.create("textures/spell/component/astral_projection.png"));
    public static final SpellEffect COUNTERSPELL = new CounterspellComponent(RLoc.create("textures/spell/component/counterspell.png"));
    public static final SpellEffect ENRAGE = new EnrageComponent(RLoc.create("textures/spell/component/enrage.png"));
    public static final SpellEffect ICE_BLOCK = new IceBlockComponent(RLoc.create("textures/spell/component/ice_block.png"));
    public static final SpellEffect LIFE_LINK = new LifeLinkComponent(RLoc.create("textures/spell/component/life_link.png"));
    public static final SpellEffect PATH = new PathComponent(RLoc.create("textures/spell/component/path.png"));
    public static final SpellEffect PLOW = new PlowComponent(RLoc.create("textures/spell/component/plow.png"));
    public static final SpellEffect POLYMORPH = new PolymorphComponent(RLoc.create("textures/spell/component/polymorph.png"));
    public static final SpellEffect STRIP = new StripComponent(RLoc.create("textures/spell/component/strip.png"));
    public static final SpellEffect TRANSFUSE = new TransfuseComponent(RLoc.create("textures/spell/component/transfuse.png"));
    public static final SpellEffect TREE_STRIDE = new TreeStrideComponent(RLoc.create("textures/spell/component/tree_stride.png"));

    @SubscribeEvent
    public static void registerComponents(RegisterEvent event) {
        if (!ModList.get().isLoaded(ManaAndArtificeMod.ID)) {
            return;
        }

        event.register(((IForgeRegistry) Registries.SpellEffect.get()).getRegistryKey(), (helper) -> {
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
            event.register(((IForgeRegistry)Registries.SpellEffect.get()).getRegistryKey(), (helper) -> {
                helper.register(RLoc.create("components/polymorph"), SpellsRegistry.POLYMORPH);
            });
        }
    }

    @SubscribeEvent
    public static void registerShapes(RegisterEvent event) {
        if (!ModList.get().isLoaded(ManaAndArtificeMod.ID)) {
            return;
        }

        event.register(((IForgeRegistry) Registries.Shape.get()).getRegistryKey(), (helper) -> {
            helper.register(RLoc.create("shapes/familiar"), FAMILIAR);
            helper.register(RLoc.create("shapes/shared"), SHARED);
        });
    }
}
