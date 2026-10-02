package com.mrpup.clumapi.component;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.List;

public class ComponentItemHandler implements ResourceHandler<ItemResource>, IndexModifier<ItemResource> {

    private final InventoryComponent inventoryComponent;

    public ComponentItemHandler(InventoryComponent inventoryComponent) {
        this.inventoryComponent = inventoryComponent;
    }

    private Slot slot(int index) {
        List<Slot> slots = inventoryComponent.getSlots();
        if (index < 0 || index >= slots.size()) {
            throw new IndexOutOfBoundsException("Invalid slot: " + index);
        }
        return slots.get(index);
    }


    @Override
    public void set(int index, ItemResource resource, int amount) {
        if (resource.isEmpty() || amount <= 0) {
            inventoryComponent.getContainer().setItem(index, ItemStack.EMPTY);
            return;
        }

        inventoryComponent.getContainer().setItem(index, resource.toStack(amount));
    }

    @Override
    public int size() {
        return inventoryComponent.getContainer().getContainerSize();
    }

    @Override
    public ItemResource getResource(int index) {
        return ItemResource.of(inventoryComponent.getContainer().getItem(index) );
    }


    @Override
    public long getAmountAsLong(int index) {
         return inventoryComponent.getContainer().getItem(index).getCount();
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        Slot slot = slot(index);
        ItemStack stack = resource.toStack(1);
        return Math.min(slot.getMaxStackSize(stack), stack.getMaxStackSize());
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return slot(index).mayPlace(resource.toStack(1));
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (amount <= 0 || resource.isEmpty()) {
            return 0;
        }
        Slot targetSlot = slot(index);
        ItemStack incoming = resource.toStack((int) Math.min(amount, Integer.MAX_VALUE));

        if (!targetSlot.mayPlace(incoming)) {
            return 0;
        }

        ItemStack existing = inventoryComponent.getContainer().getItem(index);
        int maxStackSize = Math.min(targetSlot.getMaxStackSize(incoming), incoming.getMaxStackSize());
        int currentCount = existing.isEmpty() ? 0 : existing.getCount();
        int space = maxStackSize - currentCount;

        if (space <= 0) {
            return 0;
        }

        if (!existing.isEmpty() && !ItemStack.isSameItemSameComponents(existing, incoming)) {
            return 0;
        }

        int inserted = Math.min(space, incoming.getCount());
        if (inserted > 0) {
            if (existing.isEmpty()) {
                inventoryComponent.getContainer().setItem(index, incoming.copyWithCount(inserted));
            } else {
                existing.grow(inserted);
            }
        }

        return inserted;
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (amount <= 0 || resource.isEmpty()) {
            return 0;
        }

        Slot targetSlot = slot(index);
        ItemStack existing = inventoryComponent.getContainer().getItem(index);
        if (existing.isEmpty()) {
            return 0;
        }

        if (!ItemStack.isSameItemSameComponents(existing, resource.toStack(1))) {
            return 0;
        }

        if (!targetSlot.mayPickup(null)) {
            return 0;
        }

        int extracted = (int) Math.min(amount, existing.getCount());
        if (extracted > 0) {
            existing.shrink(extracted);
            if (existing.isEmpty()) {
                inventoryComponent.getContainer().setItem(index, ItemStack.EMPTY);
            }
        }

        return extracted;
    }
}
