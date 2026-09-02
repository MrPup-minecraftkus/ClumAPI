package com.mrpup.clumapi.items;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RegItems {
    public static final Map<String, ItemEntry> ITEMS_STORAGE = new HashMap<>();
    public record ItemEntry(DeferredItem<Item> item, ItemsProperties props) {}

    private static DeferredRegister.Items  ITEMS;
    private static String currentModId;

    public static Collection<DeferredHolder<Item, ? extends Item>> getRegisteredItems() {
        return ITEMS.getEntries();
    }

    public static void init(String modId, IEventBus modEventBus) {
        currentModId = modId;
        ITEMS = DeferredRegister.createItems(modId);
        ITEMS.register(modEventBus);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Item> ItemsHolder<T> reg(String name, Supplier<T> itemSupplier) {
        if (ITEMS == null) {
            throw new RuntimeException("RegItems: Register is not initialized! Call init() first.");
        }

        DeferredItem<T> item = ITEMS.register(name, itemSupplier);

        ITEMS_STORAGE.put(name, new ItemEntry((DeferredItem<Item>) item, null));

        return new ItemsHolder<>(item);
    }

}
