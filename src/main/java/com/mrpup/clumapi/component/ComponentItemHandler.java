package com.mrpup.clumapi.component;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.List;

public class ComponentItemHandler implements IItemHandlerModifiable {

    private final InventoryComponent inventoryComponent;

    public ComponentItemHandler(InventoryComponent inventoryComponent) {
        this.inventoryComponent = inventoryComponent;
    }

    private Slot slot(int index) {
        List<Slot> slots = inventoryComponent.getSlots();
        return slots.get(index);
    }

    @Override
    public int getSlots() {
        return inventoryComponent.getSlots().size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventoryComponent.getContainer().getItem(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;

        Slot targetSlot = slot(slot);
        if (!targetSlot.mayPlace(stack)) {
            return stack;
        }

        ItemStack existing = inventoryComponent.getContainer().getItem(slot);
        int maxStackSize = Math.min(targetSlot.getMaxStackSize(stack), stack.getMaxStackSize());

        int limit = existing.isEmpty() ? maxStackSize : maxStackSize - existing.getCount();
        if (limit <= 0) return stack;

        boolean sameItem = existing.isEmpty() || ItemStack.isSameItemSameComponents(existing, stack);
        if (!sameItem) return stack;

        int toInsert = Math.min(limit, stack.getCount());

        if (!simulate) {
            if (existing.isEmpty()) {
                inventoryComponent.getContainer().setItem(slot, stack.copyWithCount(toInsert));
            } else {
                existing.grow(toInsert);
            }
        }

        ItemStack remainder = stack.copy();
        remainder.shrink(toInsert);
        return remainder;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) return ItemStack.EMPTY;

        ItemStack existing = inventoryComponent.getContainer().getItem(slot);
        if (existing.isEmpty()) return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getCount());
        ItemStack result = existing.copyWithCount(toExtract);

        if (!simulate) {
            existing.shrink(toExtract);
            if (existing.isEmpty()) {
                inventoryComponent.getContainer().setItem(slot, ItemStack.EMPTY);
            }
        }

        return result;
    }

    @Override
    public int getSlotLimit(int slot) {
        return slot(slot).getMaxStackSize();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return slot(slot).mayPlace(stack);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        inventoryComponent.getContainer().setItem(slot, stack);
    }
}
