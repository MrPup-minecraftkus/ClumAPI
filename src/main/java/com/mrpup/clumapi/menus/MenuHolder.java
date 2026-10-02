package com.mrpup.clumapi.menus;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MenuHolder<T extends AbstractContainerMenu> {

    private final DeferredHolder<MenuType<?>, MenuType<T>> holder;

    public MenuHolder(DeferredHolder<MenuType<?>, ?> holder) {
        this.holder = (DeferredHolder<MenuType<?>, MenuType<T>>) holder;
    }

    public MenuType<T> get() {
        return holder.get();
    }
}
