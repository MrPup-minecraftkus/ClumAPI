package com.mrpup.clumapi;

import com.mrpup.clumapi.fluids.FluidsHolder;
import com.mrpup.clumapi.fluids.RegFluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ClumAPI.MOD_ID, dist = Dist.CLIENT)
public class ClumAPIClient {

    public ClumAPIClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    public static void initClientAPI(IEventBus modEventBus) {
        modEventBus.addListener(ClumAPIClient::onRegisterClientExtensions);
    }

    private static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        for (FluidsHolder holder : RegFluids.FLUID_STORAGE.values()) {
            IClientFluidTypeExtensions extensions = RegFluids.getClientExtensions().get(holder.getName());
            if (extensions != null) {
                event.registerFluidType(extensions, holder.getFluidType());
            }
        }
    }
}
