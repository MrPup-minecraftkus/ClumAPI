package com.mrpup.clumapi.helper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class BlockHelper {

    static Block block;
    static ResourceLocation blockKey;

    public static Block getBlockFromLoc(ResourceLocation loc) {
        return block = BuiltInRegistries.BLOCK.get(loc);
    }

    public static ResourceLocation getBlockKey(Block block) {
        return blockKey = BuiltInRegistries.BLOCK.getKey(block);
    }
}
