package org.sosly.arcaneadditions.blocks;

import com.mna.blocks.tileentities.renderers.wizard_lab.WizardLabRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.sosly.arcaneadditions.ArcaneAdditions;
import org.sosly.arcaneadditions.blocks.tileentities.ScribesBenchTile;
import org.sosly.arcaneadditions.models.ScribesBenchModel;

public class TileEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ArcaneAdditions.MOD_ID);
    public static final RegistryObject<BlockEntityType<ScribesBenchTile>> SCRIBES_BENCH = TILE_ENTITIES.register("scribes_bench", () -> Builder.of(ScribesBenchTile::new, new Block[]{BlockRegistry.SCRIBES_BENCH.get()}).build(null));

    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        BlockEntityRenderers.register(SCRIBES_BENCH.get(), (ctx) -> new WizardLabRenderer<>(ctx, new ScribesBenchModel()));
    }
}
