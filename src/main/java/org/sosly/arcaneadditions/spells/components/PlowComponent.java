/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.spells.components;
import com.mna.api.affinity.Affinity;
import com.mna.api.sound.SFX;
import com.mna.api.spells.ComponentApplicationResult;
import com.mna.api.spells.SpellPartTags;
import com.mna.api.spells.base.IModifiedSpellPart;
import com.mna.api.spells.parts.SpellEffect;
import com.mna.api.spells.targeting.SpellContext;
import com.mna.api.spells.targeting.SpellSource;
import com.mna.api.spells.targeting.SpellTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.server.level.ServerLevel;
import org.sosly.arcaneadditions.recipes.PlowTransformationRecipe;
import org.sosly.arcaneadditions.recipes.RecipeRegistry;

import java.util.List;

public class PlowComponent extends SpellEffect {
    public PlowComponent(ResourceLocation guiIcon) {
        super(guiIcon);
    }

    @Override
    public ComponentApplicationResult ApplyEffect(SpellSource caster, SpellTarget target, IModifiedSpellPart<SpellEffect> mods, SpellContext context) {
        if (!target.isBlock()) {
            return ComponentApplicationResult.FAIL;
        }

        Level level = context.getLevel();
        BlockPos blockPos = target.getBlock();
        BlockState state = level.getBlockState(blockPos);
        
        if (level.isEmptyBlock(blockPos)) {
            return ComponentApplicationResult.FAIL;
        }
        
        if (!level.getFluidState(blockPos).isEmpty()) {
            return ComponentApplicationResult.FAIL;
        }
        
        if (state.getBlock() instanceof EntityBlock) {
            return ComponentApplicationResult.FAIL;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return ComponentApplicationResult.SUCCESS;
        }

        PlowTransformationRecipe recipe = findRecipe(serverLevel, state);
        if (recipe == null) {
            return ComponentApplicationResult.FAIL;
        }
        
        if (recipe.needsAir() && !level.isEmptyBlock(blockPos.above())) {
            return ComponentApplicationResult.FAIL;
        }

        level.setBlock(blockPos, recipe.getOutput(), 11);
        
        for (ItemStack drop : recipe.getDrops()) {
            Block.popResource(level, blockPos, drop.copy());
        }
        
        return ComponentApplicationResult.SUCCESS;
    }

    private PlowTransformationRecipe findRecipe(ServerLevel level, BlockState state) {
        RecipeManager recipeManager = level.getRecipeManager();
        List<PlowTransformationRecipe> recipes = recipeManager.getAllRecipesFor(RecipeRegistry.PLOW_TRANSFORMATION_TYPE.get());
        
        for (PlowTransformationRecipe recipe : recipes) {
            if (recipe.matches(state)) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public Affinity getAffinity() {
        return Affinity.WATER;
    }

    @Override
    public SpellPartTags getUseTag() {
        return SpellPartTags.UTILITY;
    }

    @Override
    public float initialComplexity() {
        return 5.0F;
    }

    @Override
    public int requiredXPForRote() {
        return 100;
    }

    @Override
    public SoundEvent SoundEffect() {
        return SFX.Spell.Cast.WATER;
    }

}
