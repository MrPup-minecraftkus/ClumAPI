package com.mrpup.clumapi.helper;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class OutputHelper {
    private OutputHelper() {}

    public static boolean canFitAll(Container container, List<ItemStack> results) {
        return tryInsert(container, snapshot(container), results);
    }

    public static boolean canFit(Container container, ItemStack result) {
        return canFitAll(container, List.of(result));
    }

    public static boolean insertAll(Container container, List<ItemStack> results) {
        ItemStack[] sim = snapshot(container);
        if (!tryInsert(container, sim, results)) return false;

        for (int i = 0; i < sim.length; i++) {
            container.setItem(i, sim[i]);
        }
        container.setChanged();
        return true;
    }

    private static ItemStack[] snapshot(Container container) {
        ItemStack[] slots = new ItemStack[container.getContainerSize()];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = container.getItem(i).copy();
        }
        return slots;
    }

    private static boolean tryInsert(Container container, ItemStack[] slots, List<ItemStack> results) {
        for (ItemStack result : results) {
            if (result.isEmpty()) continue;

            ItemStack remaining = result.copy();

            for (int i = 0; i < slots.length && !remaining.isEmpty(); i++) {
                ItemStack slot = slots[i];
                if (slot.isEmpty()) continue;
                if (!container.canPlaceItem(i, remaining)) continue;
                if (!ItemStack.isSameItemSameComponents(slot, remaining)) continue;

                int limit = limit(container, slot);
                int space = limit - slot.getCount();
                if (space <= 0) continue;

                int moved = Math.min(space, remaining.getCount());
                slot.grow(moved);
                remaining.shrink(moved);
            }

            for (int i = 0; i < slots.length && !remaining.isEmpty(); i++) {
                if (!slots[i].isEmpty()) continue;
                if (!container.canPlaceItem(i, remaining)) continue;

                int moved = Math.min(limit(container, remaining), remaining.getCount());
                slots[i] = remaining.copyWithCount(moved);
                remaining.shrink(moved);
            }

            if (!remaining.isEmpty()) return false;
        }
        return true;
    }

    private static int limit(Container container, ItemStack stack) {
        return Math.min(container.getMaxStackSize(), stack.getMaxStackSize());
    }
}
