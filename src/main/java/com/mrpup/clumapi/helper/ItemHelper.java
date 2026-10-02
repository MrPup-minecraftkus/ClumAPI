package com.mrpup.clumapi.helper;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Optional;

public class ItemHelper {

    static Item item;
    static Identifier itemKey;
    static String name;

    public static Item getItemFromLoc(Identifier loc) {
        return item = BuiltInRegistries.ITEM.get(loc).map(Holder.Reference::value).orElse(Items.AIR);
    }

    public static Item getItemFromString(String loc) {
        Identifier itemId = Identifier.parse(loc);
        return BuiltInRegistries.ITEM.get(itemId).map(Holder.Reference::value).orElse(Items.AIR);
    }

    public static Optional<Holder.Reference<Item>> getItemHolderFromLoc(Identifier loc) {
        return BuiltInRegistries.ITEM.get(loc);
    }

    public static boolean itemExists(String id) {
        Identifier loc = Identifier.tryParse(id);
        return loc != null && BuiltInRegistries.ITEM.containsKey(loc);
    }

    public static Identifier getItemKey(Item item) {
        return itemKey = BuiltInRegistries.ITEM.getKey(item);
    }

    public static String getItemName(Item item) {
        return name = BuiltInRegistries.ITEM.getKey(item).toString();
    }
}
