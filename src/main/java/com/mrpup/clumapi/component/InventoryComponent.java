package com.mrpup.clumapi.component;

import com.mrpup.clumapi.component.slot.FilteredSlot;
import com.mrpup.clumapi.component.slot.OutputSlot;
import com.mrpup.clumapi.component.slot.SlotTypes;
import com.mrpup.clumapi.menus.BaseComponentMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class InventoryComponent implements IGuiComponent, ISerializableComponent {
    private final String name;
    private final int x, y;
    private final int cols, rows;
    private final SimpleContainer container;
    private final SlotTypes.SlotType slotType;
    private final List<Slot> slots;
    private final List<Integer> outputSlotIndices = new ArrayList<>();

    public InventoryComponent(String name, int x, int y, int cols, int rows, SlotTypes.SlotType slotType) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.cols = cols;
        this.rows = rows;
        this.container = new SimpleContainer(cols * rows);
        this.slotType = slotType;
        this.slots = new ArrayList<>();
        createSlots();
    }

    public InventoryComponent(String name, int x, int y, int cols, int rows) {
        this(name, x, y, cols, rows, SlotTypes.SLOT_ITEM);
    }

    private void createSlots() {
        slots.clear();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int index = row * cols + col;
                int slotX = x + col * 18;
                int slotY = y + row * 18;

                Slot slot;
                if (outputSlotIndices.contains(index)) {
                    slot = new OutputSlot(container, index, slotX, slotY, slotType.getOutputVariant());
                } else {
                    slot = new FilteredSlot(container, index, slotX, slotY, slotType);
                }
                slots.add(slot);
            }
        }
    }

    public static int findFirstNonEmptySlot(InventoryComponent component) {
        for (int i = 0; i < component.getContainer().getContainerSize(); i++) {
            if (!component.getContainer().getItem(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }


    public boolean addOrStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack existing = container.getItem(i);
            if (!existing.isEmpty() && ItemStack.isSameItemSameComponents(existing, stack)
                    && existing.getCount() < existing.getMaxStackSize()) {
                int space = existing.getMaxStackSize() - existing.getCount();
                int toAdd = Math.min(space, stack.getCount());
                existing.grow(toAdd);
                stack.shrink(toAdd);
                if (stack.isEmpty()) {
                    return true;
                }
            }
        }

        for (int i = 0; i < container.getContainerSize(); i++) {
            if (container.getItem(i).isEmpty()) {
                container.setItem(i, stack.copy());
                stack.setCount(0);
                return true;
            }
        }

        return stack.isEmpty();
    }

    public boolean canFit(ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack existing = container.getItem(i);
            if (existing.isEmpty()) {
                return true;
            }
            if (ItemStack.isSameItemSameComponents(existing, stack)
                    && existing.getCount() < existing.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void addSlots(BaseComponentMenu menu) {
        for (Slot slot : slots) {
            menu.addPublicSlot(slot);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int leftPos, int topPos) {
        for (Slot slot : slots) {
            SlotTypes.SlotType typeToRender;

            if (slot instanceof OutputSlot outputSlot) {
                typeToRender = outputSlot.getSlotType();
            } else if (slot instanceof FilteredSlot filteredSlot) {
                typeToRender = filteredSlot.getSlotType();
            } else {
                typeToRender = slotType;
            }

            int slotX = leftPos + slot.x;
            int slotY = topPos + slot.y;

            typeToRender.render(graphics, slotX, slotY);
        }
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public String getSaveKey() {
        return name;
    }

    @Override
    public void saveComponent(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag itemsTag = new ListTag();

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                CompoundTag slotTag = new CompoundTag();
                slotTag.putInt("slot", i);
                slotTag.put("item", stack.save(registries, new CompoundTag()));
                itemsTag.add(slotTag);
            }
        }

        tag.put(getSaveKey(), itemsTag);
    }

    @Override
    public void loadComponent(CompoundTag tag, HolderLookup.Provider registries) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            container.setItem(i, ItemStack.EMPTY);
        }

        if (!tag.contains(getSaveKey())) {
            return;
        }

        ListTag itemsTag = tag.getList(getSaveKey(), 10);

        for (int i = 0; i < itemsTag.size(); i++) {
            CompoundTag slotTag = itemsTag.getCompound(i);
            int slot = slotTag.getInt("slot");

            if (slot >= 0 && slot < container.getContainerSize()) {
                ItemStack stack = ItemStack.parse(registries, slotTag.getCompound("item"))
                        .orElse(ItemStack.EMPTY);
                container.setItem(slot, stack);
            }
        }
    }

    public InventoryComponent setAllOutput() {
        outputSlotIndices.clear();
        for (int i = 0; i < cols * rows; i++) {
            outputSlotIndices.add(i);
        }
        createSlots();
        return this;
    }

    public InventoryComponent setOutputSlots(int... indices) {
        outputSlotIndices.clear();
        for (int index : indices) {
            outputSlotIndices.add(index);
        }
        createSlots();
        return this;
    }

    public InventoryComponent addOutputSlot(int row, int col) {
        outputSlotIndices.add(row * cols + col);
        createSlots();
        return this;
    }



    public List<Slot> getSlots() {
        return slots;
    }

    public SimpleContainer getContainer() {
        return container;
    }

    public SlotTypes.SlotType getSlotType() {
        return slotType;
    }

    public String getName() {
        return name;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }
}
