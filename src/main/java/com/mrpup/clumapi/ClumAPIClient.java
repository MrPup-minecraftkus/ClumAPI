package com.mrpup.clumapi;

import com.mrpup.clumapi.datagen.DataHelper;
import com.mrpup.clumapi.fluids.FluidsHolder;
import com.mrpup.clumapi.fluids.RegFluids;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;

@Mod(value = ClumAPI.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = ClumAPI.MOD_ID, value = Dist.CLIENT)
public class ClumAPIClient {

    public ClumAPIClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    public static void initClientAPI(String modId, IEventBus modEventBus) {
        modEventBus.addListener(ClumAPIClient::onBlockColors);
        modEventBus.addListener(ClumAPIClient::onRegisterClientExtensions);
        modEventBus.addListener(ClumAPIClient::registerFluidModels);
        modEventBus.addListener((GatherDataEvent.Client event) -> DataHelper.registerApiDataGenClient(event, modId));
    }

    private static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        for (FluidsHolder holder : RegFluids.FLUID_STORAGE.values()) {
            IClientFluidTypeExtensions extensions = RegFluids.getClientExtensions().get(holder.getName());
            if (extensions != null) {
                event.registerFluidType(extensions, holder.getFluidType());
            }
        }
    }

    public static void registerFluidModels(RegisterFluidModelsEvent event) {
        for (FluidsHolder holder : RegFluids.FLUID_STORAGE.values()) {

            FluidModel.Unbaked model = new FluidModel.Unbaked(
                    new Material(Identifier.fromNamespaceAndPath("minecraft", "block/water_still"), true),
                    new Material(Identifier.fromNamespaceAndPath("minecraft", "block/water_flow")),
                    null,
                    (FluidTintSource) state -> holder.getTintColor()
            );

            event.register(model, holder.getSource(), holder.getFlowing());
        }
    }

    private static void onBlockColors(RegisterColorHandlersEvent.BlockTintSources event) {
        for (FluidsHolder holder : RegFluids.FLUID_STORAGE.values()) {
            event.register(
                    List.of(BlockTintSources.constant(holder.getTintColor())),
                    holder.getBlock()
            );
        }
    }
}
