/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class PlowTransformationSerializer implements RecipeSerializer<PlowTransformationRecipe> {
    @Override
    public PlowTransformationRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        String inputId = GsonHelper.getAsString(json, "input");
        Block input = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(inputId));
        if (input == null) {
            throw new IllegalArgumentException("Unknown block: " + inputId);
        }

        String outputId = GsonHelper.getAsString(json, "output");
        Block outputBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(outputId));
        if (outputBlock == null) {
            throw new IllegalArgumentException("Unknown block: " + outputId);
        }
        BlockState output = outputBlock.defaultBlockState();

        List<ItemStack> drops = new ArrayList<>();
        if (json.has("drops")) {
            for (JsonElement element : GsonHelper.getAsJsonArray(json, "drops")) {
                JsonObject dropJson = element.getAsJsonObject();
                String itemId = GsonHelper.getAsString(dropJson, "item");
                int count = GsonHelper.getAsInt(dropJson, "count", 1);
                
                var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
                if (item == null) {
                    throw new IllegalArgumentException("Unknown item: " + itemId);
                }
                
                drops.add(new ItemStack(item, count));
            }
        }

        boolean needsAir = GsonHelper.getAsBoolean(json, "needs_air", true);

        return new PlowTransformationRecipe(recipeId, input, output, drops, needsAir);
    }

    @Override
    public PlowTransformationRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        ResourceLocation inputId = buffer.readResourceLocation();
        Block input = ForgeRegistries.BLOCKS.getValue(inputId);
        
        ResourceLocation outputId = buffer.readResourceLocation();
        Block outputBlock = ForgeRegistries.BLOCKS.getValue(outputId);
        BlockState output = outputBlock != null ? outputBlock.defaultBlockState() : null;
        
        int dropCount = buffer.readVarInt();
        List<ItemStack> drops = new ArrayList<>();
        for (int i = 0; i < dropCount; i++) {
            drops.add(buffer.readItem());
        }
        
        boolean needsAir = buffer.readBoolean();
        
        return new PlowTransformationRecipe(recipeId, input, output, drops, needsAir);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, PlowTransformationRecipe recipe) {
        buffer.writeResourceLocation(ForgeRegistries.BLOCKS.getKey(recipe.input));
        buffer.writeResourceLocation(ForgeRegistries.BLOCKS.getKey(recipe.output.getBlock()));
        
        buffer.writeVarInt(recipe.drops.size());
        for (ItemStack drop : recipe.drops) {
            buffer.writeItem(drop);
        }
        
        buffer.writeBoolean(recipe.needsAir);
    }
}