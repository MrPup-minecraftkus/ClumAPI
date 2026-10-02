package com.mrpup.clumapi.recipe;

import com.mrpup.clumapi.helper.ItemHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.function.Function;

public class MachineRecipeBuilder<T extends Recipe<?>> implements RecipeBuilder {

    private final Ingredient[] inputs;
    private final Identifier outputId;
    private final String folder;

    private ItemStackTemplate output;
    private SizedFluidIngredient fluidInput;

    private final Function<MachineRecipeBuilder<T>, T> factory;

    private final Advancement.Builder advancement =
            Advancement.Builder.recipeAdvancement();

    private String group = "";

    private MachineRecipeBuilder(int inputCount, ItemStackTemplate output, Identifier outputId, String folder, Function<MachineRecipeBuilder<T>, T> factory) {
        this.inputs = new Ingredient[inputCount];
        this.output = output;
        this.outputId = outputId;
        this.folder = folder;
        this.factory = factory;
    }

    public static <T extends Recipe<?>> MachineRecipeBuilder<T> create(int inputCount, ItemLike output, String folder, Function<MachineRecipeBuilder<T>, T> factory) {
        return new MachineRecipeBuilder<>(
                inputCount,
                new ItemStackTemplate(output.asItem()),
                ItemHelper.getItemKey(output.asItem()),
                folder,
                factory
        );
    }

    public static <T extends Recipe<?>> MachineRecipeBuilder<T> create(int inputCount, ItemStackTemplate output, Identifier outputId, String folder, Function<MachineRecipeBuilder<T>, T> factory) {
        return new MachineRecipeBuilder<>(
                inputCount,
                output,
                outputId,
                folder,
                factory
        );
    }

    public MachineRecipeBuilder<T> input(int slot, ItemLike item) {
        return input(slot, Ingredient.of(item));
    }

    public MachineRecipeBuilder<T> input(int slot, TagKey<Item> tag, HolderGetter<Item> items) {
        return input(slot, Ingredient.of(items.getOrThrow(tag)));
    }

    public MachineRecipeBuilder<T> input(int slot, Ingredient ingredient) {
        if (slot < 0 || slot >= inputs.length) {
            throw new IllegalArgumentException(
                    "Invalid input slot: " + slot
            );
        }

        inputs[slot] = ingredient;
        return this;
    }

    public MachineRecipeBuilder<T> fluid(Fluid fluid, int amount) {
        fluidInput = SizedFluidIngredient.of(fluid, amount);
        return this;
    }

    public MachineRecipeBuilder<T> fluid(TagKey<Fluid> tag, int amount) {
        fluidInput = new SizedFluidIngredient(
                FluidIngredient.of(
                        RegRecipes.getFluidLookup().getOrThrow(tag)
                ),
                amount
        );

        return this;
    }

    public MachineRecipeBuilder<T> count(int count) {
        output = output.withCount(count);
        return this;
    }

    public MachineRecipeBuilder<T> group(String group) {
        this.group = group;
        return this;
    }

    public MachineRecipeBuilder<T> unlockedBy(String name, Criterion<?> criterion) {
        advancement.addCriterion(name, criterion);
        return this;
    }

    public Ingredient input(int slot) {
        return inputs[slot];
    }

    public Ingredient[] inputs() {
        return inputs;
    }

    public ItemStackTemplate output() {
        return output;
    }

    public SizedFluidIngredient fluidInput() {
        return fluidInput;
    }

    public String group() {
        return group;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return ResourceKey.create(
                Registries.RECIPE,
                outputId.withPrefix(folder)
        );
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> id) {
        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i] == null) {
                throw new IllegalStateException(
                        "Recipe " + id + " is missing input slot " + i
                );
            }
        }

        Identifier recipeId = id.identifier().withPrefix(folder);

        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(
                        Registries.RECIPE,
                        recipeId
                );

        T recipe = factory.apply(this);

        recipeOutput.accept(
                recipeKey,
                recipe,
                advancement
                        .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeKey))
                        .rewards(AdvancementRewards.Builder.recipe(recipeKey))
                        .requirements(AdvancementRequirements.Strategy.OR)
                        .build(recipeId.withPrefix("recipes/misc/"))
        );
    }
}
