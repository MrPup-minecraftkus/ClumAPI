package com.mrpup.clumapi.plugin.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class JeiSlotLayout<R> {

    @FunctionalInterface
    private interface SlotEntry<R> {
        void apply(IRecipeLayoutBuilder builder, R recipe);
    }

    private final List<SlotEntry<R>> entries = new ArrayList<>();

    public JeiSlotLayout<R> itemSlot(RecipeIngredientRole role, int x, int y, Function<R, Ingredient> ingredient) {
        entries.add((builder, recipe) ->
                builder.addSlot(role, x, y).addIngredients(ingredient.apply(recipe)));
        return this;
    }

    public JeiSlotLayout<R> outputItemSlot(int x, int y, Function<R, ItemStack> stack) {
        entries.add((builder, recipe) -> {
            ItemStack out = stack.apply(recipe);
            if (!out.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.OUTPUT, x, y).addItemStack(out);
            }
        });
        return this;
    }

    public JeiSlotLayout<R> fluidSlot(RecipeIngredientRole role, int x, int y, int width, int height,
                                      Function<R, FluidStack[]> fluids, Function<R, Integer> amount,
                                      IDrawable overlay, int overlayX, int overlayY) {
        entries.add((builder, recipe) ->
                builder.addSlot(role, x, y)
                        .setFluidRenderer((long) amount.apply(recipe), false, width, height)
                        .setOverlay(overlay, overlayX, overlayY)
                        .addIngredients(NeoForgeTypes.FLUID_STACK, List.of(fluids.apply(recipe))));
        return this;
    }

    public void apply(IRecipeLayoutBuilder builder, R recipe) {
        for (SlotEntry<R> entry : entries) {
            entry.apply(builder, recipe);
        }
    }
}
