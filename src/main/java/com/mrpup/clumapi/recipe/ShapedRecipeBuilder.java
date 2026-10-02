package com.mrpup.clumapi.recipe;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.level.ItemLike;

public final class ShapedRecipeBuilder {

    private ShapedRecipeBuilder() {
    }

    public static net.minecraft.data.recipes.ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result) {
        return net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(
                RegRecipes.getItemLookup(),
                category,
                result
        );
    }

    public static net.minecraft.data.recipes.ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result, int count) {
        return net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(
                RegRecipes.getItemLookup(),
                category,
                result,
                count
        );
    }
}