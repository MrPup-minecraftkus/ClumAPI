package com.mrpup.clumapi.menus;

import com.mrpup.clumapi.blocks.entity.ComponentBlockEntity;
import com.mrpup.clumapi.component.IGuiComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ComponentMenu extends BaseComponentMenu  {

    private final ComponentBlockEntity blockEntity;
    public static final int GUI_HEIGHT = 184;

    public ComponentMenu(MenuType<?> type, int windowId, Inventory playerInv, ComponentBlockEntity blockEntity) {
        super(type, windowId);
        this.blockEntity = blockEntity;

        for (IGuiComponent component : blockEntity.getComponents()) {
            component.addSlots(this);
            for (DataSlot dataSlot : component.getDataSlots()) {
                addDataSlot(dataSlot);
            }
        }

        addPlayerInventory(playerInv);
    }

    private void addPlayerInventory(Inventory inv) {
        int startY = GUI_HEIGHT - 82;

        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                this.addPublicSlot(new Slot(inv,
                        col + row * 9 + 9,
                        8 + col * 18,
                        startY + row * 18));

        for (int col = 0; col < 9; col++)
            this.addPublicSlot(new Slot(inv, col,
                    8 + col * 18,
                    startY + 58));
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack originalStack = ItemStack.EMPTY;
        Slot sourceSlot = this.slots.get(index);

        if (sourceSlot != null && sourceSlot.hasItem()) {
            ItemStack sourceStack = sourceSlot.getItem();
            originalStack = sourceStack.copy();

            int totalSlots = this.slots.size();
            int playerInvStart = totalSlots - 36;

            if (index < playerInvStart) {
                if (!this.moveItemStackTo(sourceStack, playerInvStart, totalSlots, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(sourceStack, 0, playerInvStart, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (sourceStack.isEmpty()) {
                sourceSlot.set(ItemStack.EMPTY);
            } else {
                sourceSlot.setChanged();
            }

            if (sourceStack.getCount() == originalStack.getCount()) {
                return ItemStack.EMPTY;
            }

            sourceSlot.onTake(player, sourceStack);
        }

        return originalStack;
    }

    public ComponentBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        for (IGuiComponent component : blockEntity.getComponents()) {
            for (int btnId : component.getButtonIds()) {
                if (btnId == id) {
                    component.onButtonClicked(player, id);
                    return true;
                }
            }
        }
        return super.clickMenuButton(player, id);
    }
}