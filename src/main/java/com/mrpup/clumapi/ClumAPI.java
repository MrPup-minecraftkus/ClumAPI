package com.mrpup.clumapi;

import com.mojang.logging.LogUtils;
import com.mrpup.clumapi.blocks.RegBlocks;
import com.mrpup.clumapi.component.RegComponentCapabilities;
import com.mrpup.clumapi.config.RegConfig;
import com.mrpup.clumapi.datagen.DataHelper;
import com.mrpup.clumapi.fluids.RegFluids;
import com.mrpup.clumapi.items.RegItems;
import com.mrpup.clumapi.menus.RegMenus;
import com.mrpup.clumapi.recipe.RegRecipes;
import com.mrpup.clumapi.screens.RegScreens;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import org.slf4j.Logger;

@Mod(ClumAPI.MOD_ID)
public class ClumAPI {
    public static final String MOD_ID = "clumapi";

    private static final Logger LOGGER = LogUtils.getLogger();

    public ClumAPI(IEventBus modEventBus, ModContainer modContainer) {
        BestCat();
    }

    private void BestCat() {
        LOGGER.debug("all cats are beautiful, my cat -> assets/clumapi/cat.png");
    }

    public static void initAPI(String modId, IEventBus modEventBus) {
        RegBlocks.init(modId, modEventBus);
        RegItems.init(modId, modEventBus);
        RegMenus.init(modId, modEventBus);
        RegConfig.init(modId, modEventBus);
        RegFluids.init(modId, modEventBus);
        RegRecipes.init(modId, modEventBus);
        modEventBus.addListener(RegScreens::onRegisterScreens);
        modEventBus.addListener((GatherDataEvent event) -> DataHelper.registerApiDataGen(event, modId));
        modEventBus.addListener(ClumAPI::onRegisterCapabilities);
    }

    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        RegComponentCapabilities.registerAll(event);
    }
}
