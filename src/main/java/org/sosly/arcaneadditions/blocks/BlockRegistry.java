package org.sosly.arcaneadditions.blocks;

import com.mna.api.blocks.interfaces.ICutoutBlock;
import com.mna.api.blocks.interfaces.ITranslucentBlock;
import com.mna.api.items.MACreativeTabs;
import com.mna.api.items.TieredBlockItem;
import com.mna.blocks.IOffsetPlace;
import com.mna.items.OffsetPlacerItem;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.blocks.artifice.ScribesBenchBlock;
import org.sosly.arcaneadditions.models.ScribesBenchModel;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = ArcaneAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ArcaneAdditions.MOD_ID);
    public static final RegistryObject<ScribesBenchBlock> SCRIBES_BENCH = BLOCKS.register("scribes_bench", ScribesBenchBlock::new);

    @SubscribeEvent
    public static void onRegisterItems(RegisterEvent event) {
        event.register(ForgeRegistries.ITEMS.getRegistryKey(), (helper) -> {
            BLOCKS.getEntries().stream().map(RegistryObject::get).forEach((block) -> {
                Item.Properties properties = new Item.Properties();
                if (block instanceof IOffsetPlace adjuster) {
                    Objects.requireNonNull((IOffsetPlace)block);
                    OffsetPlacerItem blockItem = new OffsetPlacerItem(block, properties, adjuster::adjustPlacement);
                    helper.register(ForgeRegistries.BLOCKS.getKey(block), blockItem);
                } else {
                    TieredBlockItem blockItemx = new TieredBlockItem(block, properties);
                    helper.register(ForgeRegistries.BLOCKS.getKey(block), blockItemx);
                }
            });
        });
    }

    @SubscribeEvent
    public static void fillCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == MACreativeTabs.GENERAL) {
            BLOCKS.getEntries().stream().map(RegistryObject::get).forEach((block) -> {
                ItemStack stack = new ItemStack(block);
                if (!stack.isEmpty() && stack.getCount() == 1) {
                    event.accept(stack);
                } else {
                    ArcaneAdditions.LOGGER.warn("unable to put " + block + " in the creative tab");
                }
            });
        }
    }

    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        BlockRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
            if (!(block instanceof ICutoutBlock) && !(block instanceof FlowerPotBlock)) {
                if (block instanceof ITranslucentBlock) {
                    ItemBlockRenderTypes.setRenderLayer(block, RenderType.translucent());
                }
            } else {
                ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
            }
        });
    }

    @SubscribeEvent
    public static void onRegisterSpecialModels(ModelEvent.RegisterAdditional event) {
        event.register(ScribesBenchModel.ink);
        event.register(ScribesBenchModel.lapis);
        event.register(ScribesBenchModel.source);
        event.register(ScribesBenchModel.target);
    }
}
