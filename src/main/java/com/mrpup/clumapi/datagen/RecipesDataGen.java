package com.mrpup.clumapi.datagen;

import com.mrpup.clumapi.recipe.RegRecipes;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public class RecipesDataGen extends RecipeProvider {
    private final String modId;

    public RecipesDataGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modId) {
        super(output, registries);
        this.modId = modId;
    }

    public Criterion<InventoryChangeTrigger.TriggerInstance> hasItem(ItemLike itemLike) {
        return has(itemLike);
    }

    public Criterion<InventoryChangeTrigger.TriggerInstance> hasTag(TagKey<Item> tag) {
        return has(tag);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        RegRecipes.RECIPE_STORAGE.forEach((name, entry) -> {
            ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(modId, name);

            if (entry.isSpecial()) {
                SimpleCraftingRecipeSerializer.Factory<?> recipeFactory =
                        (SimpleCraftingRecipeSerializer.Factory<?>) entry.specialFactory();

                SpecialRecipeBuilder.special(category -> recipeFactory.create(category))
                        .save(recipeOutput, recipeId);
            } else {
                if (entry.dataFactory() != null) {
                    RecipeBuilder builder = entry.dataFactory().create(this);
                    if (builder != null) {
                        builder.save(recipeOutput, recipeId);
                    }
                }
            }
        });
    }
}
