package com.mrpup.clumapi.helper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ItemHelper {

    static Item item;
    static ResourceLocation itemKey;
    static String name;

    public static Item getItemFromLoc(ResourceLocation loc) {
        return item = BuiltInRegistries.ITEM.get(loc);
    }

    public static Item getItemFromString(String loc) {
        ResourceLocation itemId = ResourceLocation.parse(loc);
        return BuiltInRegistries.ITEM.get(itemId);
    }

    public static boolean itemExists(String id) {
        ResourceLocation loc = ResourceLocation.tryParse(id);
        return loc != null && BuiltInRegistries.ITEM.containsKey(loc);
    }

    public static ResourceLocation getItemKey(Item item) {
        return itemKey = BuiltInRegistries.ITEM.getKey(item);
    }

    public static String getItemName(Item item) {
        return name = BuiltInRegistries.ITEM.getKey(item).toString();
    }
}
