package com.mrpup.clumapi.recipe;

import com.mrpup.clumapi.datagen.RecipesDataGen;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.*;
import java.util.function.Supplier;

public class RegRecipes {
    public static final Map<String, RecipeEntry> RECIPE_STORAGE = new HashMap<>();
    public static final List<DeferredHolder<RecipeSerializer<?>, ? extends RecipeSerializer<?>>> RECIPE_HOLDERS = new ArrayList<>();

    @FunctionalInterface
    public interface RecipeDataFactory {
        RecipeBuilder create(RecipesDataGen provider);
    }

    public record RecipeEntry(
            DeferredHolder<RecipeSerializer<?>, ? extends RecipeSerializer<?>> serializer,
            Object specialFactory,
            RecipeDataFactory dataFactory,
            boolean isSpecial
    ) {}

    private static String MOD_ID;
    private static DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS;
    private static DeferredRegister<RecipeType<?>> RECIPE_TYPES;

    public static void init(String modId, IEventBus modEventBus) {
        MOD_ID = modId;

        RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, modId);
        RECIPE_SERIALIZERS.register(modEventBus);

        RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, modId);
        RECIPE_TYPES.register(modEventBus);
    }

    @SuppressWarnings("unchecked")
    public static <T extends CraftingRecipe> Supplier<SimpleCraftingRecipeSerializer<T>> regCraftingSerializer(
            String name,
            SimpleCraftingRecipeSerializer.Factory<T> factory) {

        var holder = RECIPE_SERIALIZERS.register(name, () -> new SimpleCraftingRecipeSerializer<>(factory));
        RECIPE_HOLDERS.add(holder);

        RECIPE_STORAGE.put(name, new RecipeEntry(holder, factory, null, true));

        return (Supplier<SimpleCraftingRecipeSerializer<T>>) (Object) holder;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>> Supplier<RecipeSerializer<T>> regRecipeSerializer(
            String name,
            Supplier<? extends RecipeSerializer<T>> serializer) {

        var holder = RECIPE_SERIALIZERS.register(name, serializer);
        RECIPE_HOLDERS.add((DeferredHolder<RecipeSerializer<?>, ? extends RecipeSerializer<?>>) (Object) holder);

        return (Supplier<RecipeSerializer<T>>) (Object) holder;
    }

    public static <T extends Recipe<?>> Supplier<RecipeType<T>> regRecipeType(String name) {
        return RECIPE_TYPES.register(name,
                () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(MOD_ID, name)));
    }

    public static void regRecipe(String name, RecipeDataFactory factory) {
        RECIPE_STORAGE.put(name, new RecipeEntry(null, null, factory, false));
    }
}