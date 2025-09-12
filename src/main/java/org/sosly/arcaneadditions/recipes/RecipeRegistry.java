/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.recipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.sosly.arcaneadditions.ArcaneAdditions;

public class RecipeRegistry {
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = 
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ArcaneAdditions.MOD_ID);
    
    private static final DeferredRegister<RecipeType<?>> TYPES = 
        DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ArcaneAdditions.MOD_ID);

    public static final RegistryObject<RecipeType<PlowTransformationRecipe>> PLOW_TRANSFORMATION_TYPE = 
        TYPES.register("plow_transformation", 
            () -> RecipeType.simple(new ResourceLocation(ArcaneAdditions.MOD_ID, "plow_transformation")));

    public static final RegistryObject<RecipeSerializer<PlowTransformationRecipe>> PLOW_TRANSFORMATION_SERIALIZER = 
        SERIALIZERS.register("plow_transformation", PlowTransformationSerializer::new);

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
        SERIALIZERS.register(eventBus);
    }
}