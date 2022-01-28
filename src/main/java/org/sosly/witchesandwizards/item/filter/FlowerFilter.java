package org.sosly.witchesandwizards.item.filter;

import com.mna.blocks.BlockInit;
import com.mna.effects.EffectInit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;
import org.sosly.witchesandwizards.config.ClientConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class FlowerFilter {
    private static final Lazy<List<Tag<Item>>> tags = compileFlowerTags();
    public static final Tag<Item> MAGIC_FLOWERS = ItemTags.bind("wnw:magic_flowers");
    private static final Lazy<HashMap<Item, MobEffect>> FLOWER_EFFECTS = compileFlowerMap();

    private static Lazy<HashMap<Item, MobEffect>> compileFlowerMap() {
        return Lazy.of(() -> {
            HashMap<Item, MobEffect> effects = new HashMap<>();

            effects.put(MNARegistry.AUM, MobEffects.REGENERATION);
            effects.put(MNARegistry.CERUBLOSSOM, EffectInit.MANA_REGEN.get()); // todo: Is there a better way to get this effect?
            effects.put(MNARegistry.DESERT_NOVA, MobEffects.FIRE_RESISTANCE);
            effects.put(MNARegistry.TARMA_ROOT, MobEffects.JUMP);
            effects.put(MNARegistry.WAKEBLOOM, MobEffects.WATER_BREATHING);

            return effects;
        });
    }

    private static Lazy<List<Tag<Item>>> compileFlowerTags() {
        return Lazy.of(() -> {
            List<String> tagStrings = ClientConfig.Artifacts.FLOWER_TAGS.get();
            List<Tag<Item>> tags = new ArrayList<>();
            tagStrings.forEach((tag) -> {
                tags.add(ItemTags.createOptional(new ResourceLocation(tag)));
            });
            return tags;
        });
    }

    public static MobEffect getFlowerEffect(Item flower) {
        return FLOWER_EFFECTS.get().get(flower);
    }

    public static boolean anyMatch(Predicate<? super Tag<Item>> predicate) {
        return tags.get().stream().anyMatch(predicate);
    }

    @ObjectHolder("mna")
    private static class MNARegistry {
        public static final Item AUM = null;
        public static final Item CERUBLOSSOM = null;
        public static final Item DESERT_NOVA = null;
        public static final Item WAKEBLOOM = null;
        public static final Item TARMA_ROOT = null;

        public static final MobEffect MANA_REGEN = null;
    }
}
