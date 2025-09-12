/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.recipes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class PlowTransformationRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    final Block input;
    final BlockState output;
    final List<ItemStack> drops;
    final boolean needsAir;

    public PlowTransformationRecipe(ResourceLocation id, Block input, BlockState output, List<ItemStack> drops, boolean needsAir) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.drops = drops;
        this.needsAir = needsAir;
    }

    public boolean matches(BlockState state) {
        return state.getBlock() == input;
    }

    public BlockState getOutput() {
        return output;
    }

    public List<ItemStack> getDrops() {
        return drops;
    }

    public boolean needsAir() {
        return needsAir;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.PLOW_TRANSFORMATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.PLOW_TRANSFORMATION_TYPE.get();
    }
}