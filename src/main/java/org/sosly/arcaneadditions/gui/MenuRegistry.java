/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.gui;

import com.mna.gui.HUDOverlayRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.blocks.BlockRegistry;
import org.sosly.arcaneadditions.gui.menus.ScribesBenchMenu;
import org.sosly.arcaneadditions.gui.menus.TreeStrideMenu;
import org.sosly.arcaneadditions.gui.screens.ScribesBenchScreen;
import org.sosly.arcaneadditions.gui.screens.TreeStrideScreen;

@Mod.EventBusSubscriber(modid = ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ArcaneAdditions.MOD_ID);
    public static final RegistryObject<MenuType<ScribesBenchMenu>> SCRIBES_BENCH = MENUS.register(of(BlockRegistry.SCRIBES_BENCH), () -> IForgeMenuType.create(ScribesBenchMenu::new));
    public static final RegistryObject<MenuType<TreeStrideMenu>> TREE_STRIDE = MENUS.register("tree_stride", () -> new MenuType<>(TreeStrideMenu::new, FeatureFlags.DEFAULT_FLAGS));

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(MenuRegistry.SCRIBES_BENCH.get(), ScribesBenchScreen::new);
        MenuScreens.register(MenuRegistry.TREE_STRIDE.get(), TreeStrideScreen::new);
        HUDOverlayRenderer.instance = new HUDOverlayRenderer();
    }

    static <T extends Block> String of(RegistryObject<T> block) {
        return block.getId().getPath();
    }
}
