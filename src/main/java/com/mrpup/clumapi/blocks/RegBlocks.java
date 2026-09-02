package com.mrpup.clumapi.blocks;
import com.mrpup.clumapi.blocks.entity.ComponentBlockEntity;
import com.mrpup.clumapi.blocks.entity.EntityBlocksHolder;
import com.mrpup.clumapi.menus.ComponentMenu;
import com.mrpup.clumapi.screens.ComponentScreen;
import com.mrpup.clumapi.menus.MenuHolder;
import com.mrpup.clumapi.menus.RegMenus;
//import com.mrpup.clumapi.renders.RegRenderers;
import com.mrpup.clumapi.screens.RegScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegBlocks {
    public static final Map<String, BlockEntry> BLOCK_STORAGE = new HashMap<>();
    private static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES;
    public static final List<DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>>> COMPONENT_BE_TYPES = new ArrayList<>();

    public record BlockEntry(DeferredBlock<Block> block, BlocksProperties props) {}

    private static DeferredRegister.Blocks BLOCKS;
    private static DeferredRegister.Items ITEMS;

    public static Collection<DeferredHolder<Item, ? extends Item>> getRegisteredItems() {
        return ITEMS.getEntries();
    }

    public static void init(String modId, IEventBus modEventBus) {
        BLOCKS = DeferredRegister.createBlocks(modId);
        ITEMS = DeferredRegister.createItems(modId);
        BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, modId);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
    }

    public static <T extends BlockEntity> EntityBlocksHolder<T> regEntity(
            String name,
            BlockEntityType.BlockEntitySupplier<T> factory,
            BlocksHolder<? extends Block> blockHolder) {

        var holder = BLOCK_ENTITIES.register(name + "_be", () ->
                BlockEntityType.Builder.of(factory, blockHolder.get()).build(null)
        );

        return new EntityBlocksHolder<>(holder);
    }

    public static <T extends Block> BlocksHolder<T> reg(String name, Function<Block.Properties, T> blockFactory, BlocksProperties chrono) {

        DeferredBlock<T> block = BLOCKS.register(name, () -> {
            T createdBlock = blockFactory.apply(chrono.getVanilla());
            return createdBlock;
        });

        ITEMS.registerSimpleBlockItem(block);


        BLOCK_STORAGE.put(name, new BlockEntry((DeferredBlock<Block>) block, chrono));
        return new BlocksHolder<>(block);
    }

    public static <T extends ComponentBlockEntity> ComponentBlockHolder<T> regComponent(String name, Supplier<ComponentBlock<T>> blockFactory, BlocksProperties props) {

        final ComponentBlock<T>[] blockRef = new ComponentBlock[1];

        DeferredBlock<Block> deferredBlock = BLOCKS.register(name, () -> {
            blockRef[0] = blockFactory.get();
            return blockRef[0];
        });

        ITEMS.registerSimpleBlockItem(deferredBlock);

        MenuHolder<ComponentMenu> menuHolder = RegMenus.regComponent(name);

        var beHolder = BLOCK_ENTITIES.register(name + "_be", () ->
                BlockEntityType.Builder.of(
                        (pos, state) -> {
                            T be = blockRef[0].getTileFactory().create(pos, state);
                            be.setMenuType(() -> menuHolder.get());
                            return be;
                        },
                        deferredBlock.get()
                ).build(null)
        );

        COMPONENT_BE_TYPES.add(beHolder);

        RegScreens.reg(menuHolder, ComponentScreen::new);
        BLOCK_STORAGE.put(name, new BlockEntry(deferredBlock, props));

        return new ComponentBlockHolder<>(deferredBlock, beHolder, menuHolder);
    }
}