package com.mrpup.clumapi.menus;

import com.mrpup.clumapi.blocks.entity.ComponentBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;

public class RegMenus {

    private static DeferredRegister<MenuType<?>> MENUS;
    public static final Map<String, DeferredHolder<MenuType<?>, MenuType<?>>> MENU_STORAGE = new HashMap<>();

    public static void init(String modId, IEventBus modEventBus) {
        MENUS = DeferredRegister.create(Registries.MENU, modId);
        MENUS.register(modEventBus);
    }

    public static <T extends AbstractContainerMenu> MenuHolder<T> reg(
            String name,
            IContainerFactory<T> factory) {

        var holder = MENUS.register(name, () ->
                IMenuTypeExtension.create(factory)
        );

        MENU_STORAGE.put(name, (DeferredHolder) holder);
        return new MenuHolder<>(holder);
    }

    public static MenuHolder<ComponentMenu> regComponent(String name) {
        DeferredHolder[] holderRef = new DeferredHolder[1];

        var holder = MENUS.register(name, () ->
                IMenuTypeExtension.create((id, inv, buf) -> {
                    BlockPos pos = buf.readBlockPos();
                    BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);

                    if (be instanceof ComponentBlockEntity cbe) {
                        return new ComponentMenu(
                                (MenuType<ComponentMenu>) holderRef[0].get(),
                                id, inv, cbe);
                    }
                    throw new IllegalStateException("No ComponentBlockEntity at " + pos);
                })
        );

        holderRef[0] = holder;
        MENU_STORAGE.put(name, (DeferredHolder) holder);
        return new MenuHolder<>(holder);
    }
}