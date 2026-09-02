package com.mrpup.clumapi.component.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FilteredSlot extends Slot {

    private final SlotTypes.SlotType slotType;

    public FilteredSlot(Container container, int index, int x, int y, SlotTypes.SlotType type) {
        super(container, index, x, y);
        this.slotType = type;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return slotType.accepts(stack);
    }

    @Override
    public int getMaxStackSize() {
        return slotType.getMaxStackSize();
    }

    public SlotTypes.SlotType getSlotType() {
        return slotType;
    }

    public SlotTypes.SlotTexture getSlotTexture() {
        return slotType.getSlotTexture();
    }
}