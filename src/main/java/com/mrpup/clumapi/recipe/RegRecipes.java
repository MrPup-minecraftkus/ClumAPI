package com.mrpup.clumapi.recipe;

import com.mojang.serialization.MapCodec;
import com.mrpup.clumapi.datagen.RecipesDataGen;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
            Supplier<Recipe<?>> specialFactory,
            RecipeDataFactory dataFactory,
            boolean isSpecial
    ) {}

    private static String MOD_ID;
    private static DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS;
    private static DeferredRegister<RecipeType<?>> RECIPE_TYPES;
    private static HolderGetter<Item> ITEM_LOOKUP;
    private static HolderGetter<Fluid> FLUID_LOOKUP;

    public static void init(String modId, IEventBus modEventBus) {
        MOD_ID = modId;

        RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, modId);
        RECIPE_SERIALIZERS.register(modEventBus);

        RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, modId);
        RECIPE_TYPES.register(modEventBus);
    }

    public static void setItemLookup(HolderGetter<Item> lookup) {
        ITEM_LOOKUP = lookup;
    }

    public static HolderGetter<Item> getItemLookup() {
        return ITEM_LOOKUP;
    }

    public static void setFluidLookup(HolderGetter<Fluid> lookup) {
        FLUID_LOOKUP = lookup;
    }

    public static HolderGetter<Fluid> getFluidLookup() {
        return FLUID_LOOKUP;
    }

    @SuppressWarnings("unchecked")
    public static <T extends CraftingRecipe> Supplier<RecipeSerializer<T>> regCraftingSerializer(
            String name,
            MapCodec<T> codec,
            StreamCodec<RegistryFriendlyByteBuf, T> streamCodec,
            Supplier<T> factory
    ) {
        var holder = RECIPE_SERIALIZERS.register(
                name,
                () -> new RecipeSerializer<>(codec, streamCodec)
        );

        RECIPE_HOLDERS.add(holder);

        Supplier<Recipe<?>> specialFactory = () -> factory.get();

        RECIPE_STORAGE.put(
                name,
                new RecipeEntry(
                        holder,
                        specialFactory,
                        null,
                        true
                )
        );

        return (Supplier<RecipeSerializer<T>>) (Object) holder;
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
                () -> RecipeType.simple(Identifier.fromNamespaceAndPath(MOD_ID, name)));
    }

    public static void regRecipe(String name, RecipeDataFactory factory) {
        RECIPE_STORAGE.put(name, new RecipeEntry(null, null, factory, false));
    }
}