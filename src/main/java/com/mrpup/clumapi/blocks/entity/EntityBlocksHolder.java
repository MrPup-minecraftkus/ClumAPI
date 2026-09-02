package com.mrpup.clumapi.blocks.entity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public record EntityBlocksHolder<T extends BlockEntity>(
        DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> entityType) {
    public BlockEntityType<T> get() {
        return entityType.get();
    }
}
