package com.mrpup.clumapi.component.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class OutputSlot extends Slot {

    private final SlotTypes.SlotType slotType;

    public OutputSlot(Container container, int index, int x, int y, SlotTypes.SlotType type) {
        super(container, index, x, y);
        this.slotType = type;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    public SlotTypes.SlotType getSlotType() {
        return slotType;
    }
}
