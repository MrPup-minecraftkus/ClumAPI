package com.mrpup.clumapi.helper;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class BlockHelper {

    static Block block;
    static Identifier blockKey;

    public static Block getBlockFromLoc(Identifier loc) {
        return block = BuiltInRegistries.BLOCK.get(loc).map(Holder.Reference::value).orElse(Blocks.AIR);
    }

    public static Identifier getBlockKey(Block block) {
        return blockKey = BuiltInRegistries.BLOCK.getKey(block);
    }
}
