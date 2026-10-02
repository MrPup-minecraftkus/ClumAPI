package com.mrpup.clumapi.datagen;

import com.mrpup.clumapi.recipe.RegRecipes;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public class RecipesDataGen extends RecipeProvider {
    private final String modId;

    public RecipesDataGen(HolderLookup.Provider registries, RecipeOutput output, String modId) {
        super(registries, output);
        this.modId = modId;
    }

    public Criterion<InventoryChangeTrigger.TriggerInstance> hasItem(ItemLike itemLike) {
        return has(itemLike);
    }

    public Criterion<InventoryChangeTrigger.TriggerInstance> hasTag(TagKey<Item> tag) {
        return has(tag);
    }

    @Override
    protected void buildRecipes() {
        RegRecipes.setItemLookup(
                this.registries.lookupOrThrow(Registries.ITEM)
        );

        RegRecipes.setFluidLookup(
                this.registries.lookupOrThrow(Registries.FLUID)
        );

        RegRecipes.RECIPE_STORAGE.forEach((name, entry) -> {
            Identifier recipeId = Identifier.fromNamespaceAndPath(modId, name);
            ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, recipeId);

            if (entry.isSpecial()) {
                SpecialRecipeBuilder
                        .special(entry.specialFactory())
                        .save(output, recipeKey);
            }else {
                if (entry.dataFactory() != null) {
                    RecipeBuilder builder = entry.dataFactory().create(this);

                    if (builder != null) {
                        builder.save(output, recipeKey);
                    }
                }
            }
        });
    }

    public static class Runner extends RecipeProvider.Runner {
        private final String modId;

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modId) {
            super(output, registries);
            this.modId = modId;
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new RecipesDataGen(registries, output, modId);
        }

        @Override
        public String getName() {
            return "sdfsd";
        }
    }
}
