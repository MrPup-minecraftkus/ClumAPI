package com.mrpup.clumapi.menus;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public abstract class BaseComponentMenu extends AbstractContainerMenu {

    protected BaseComponentMenu(MenuType<?> type, int windowId) {
        super(type, windowId);
    }

    public Slot addPublicSlot(Slot slot) {
        return this.addSlot(slot);
    }
}
