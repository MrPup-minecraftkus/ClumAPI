package com.mrpup.clumapi.blocks;

import com.mrpup.clumapi.blocks.entity.ComponentBlockEntity;
import com.mrpup.clumapi.menus.ComponentMenu;
import com.mrpup.clumapi.menus.MenuHolder;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ComponentBlockHolder<T extends ComponentBlockEntity> {

    private final DeferredBlock<Block> block;
    private final DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> blockEntity;
    private final MenuHolder<ComponentMenu> menu;

    public ComponentBlockHolder(
            DeferredBlock<Block> block,
            DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> blockEntity,
            MenuHolder<ComponentMenu> menu) {
        this.block = block;
        this.blockEntity = blockEntity;
        this.menu = menu;
    }

    public Block getBlock() {
        return block.get();
    }

    public BlockEntityType<T> getBlockEntityType() {
        return blockEntity.get();
    }

    public MenuHolder<ComponentMenu> getMenu() {
        return menu;
    }

    public MenuType<ComponentMenu> getMenuType() {
        return menu.get();
    }
}
