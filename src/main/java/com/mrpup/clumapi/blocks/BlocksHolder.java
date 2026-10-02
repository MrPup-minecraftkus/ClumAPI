package com.mrpup.clumapi.blocks;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;

public record BlocksHolder<T extends Block>(DeferredBlock<T> block) {
    public T get() {
        return block.get();
    }
}
