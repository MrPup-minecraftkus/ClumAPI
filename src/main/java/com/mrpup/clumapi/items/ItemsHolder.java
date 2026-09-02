package com.mrpup.clumapi.items;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public record ItemsHolder<T extends Item>(DeferredItem<T> item) {
    public T get() {
        return item.get();
    }
}