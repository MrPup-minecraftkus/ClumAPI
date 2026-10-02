package com.mrpup.clumapi.screens;

import com.mrpup.clumapi.menus.MenuHolder;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.util.HashMap;
import java.util.Map;

public class RegScreens {

    public static final Map<MenuHolder<?>, MenuScreens.ScreenConstructor<?, ?>> PENDING = new HashMap<>();

    public static <M extends AbstractContainerMenu,
            S extends AbstractContainerScreen<M>> void reg(
            MenuHolder<M> menuHolder,
            MenuScreens.ScreenConstructor<M, S> screenFactory) {

        PENDING.put(menuHolder, screenFactory);
    }

    @SuppressWarnings("unchecked")
    public static void onRegisterScreens(RegisterMenuScreensEvent event) {
        PENDING.forEach((holder, factory) -> {
            event.register(
                    ((MenuHolder<AbstractContainerMenu>) holder).get(),
                    (MenuScreens.ScreenConstructor<AbstractContainerMenu, ?>) factory
            );
        });
    }
}
