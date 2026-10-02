package com.mrpup.clumapi.screens;

import com.mrpup.clumapi.blocks.RegBlocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = "clumapi", value = Dist.CLIENT)
public final class ClientScreens {

    @SubscribeEvent
    public static void onRegisterScreens(RegisterMenuScreensEvent event) {
        for (var menu : RegBlocks.COMPONENT_MENUS) {
            event.register(menu.get(), ComponentScreen::new);
        }
    }
}
