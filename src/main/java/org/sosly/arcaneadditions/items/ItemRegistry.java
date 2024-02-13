/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.items;

import com.mna.api.items.MACreativeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.items.artifice.SoulsearchersLensItem;


@Mod.EventBusSubscriber(modid = org.sosly.arcaneadditions.ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ArcaneAdditions.MOD_ID);

    public static final RegistryObject<SoulsearchersLensItem> SOULSEARCHERS_LENS = ITEMS.register("soulsearchers_lens", SoulsearchersLensItem::new);


    @SubscribeEvent
    public static void addArtifice(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == MACreativeTabs.GENERAL) {
            event.accept(SOULSEARCHERS_LENS);
        }
    }
}
