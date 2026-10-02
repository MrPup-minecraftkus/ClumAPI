package com.mrpup.clumapi.component;

import com.mrpup.clumapi.blocks.RegBlocks;
import com.mrpup.clumapi.blocks.entity.ComponentBlockEntity;
import com.mrpup.clumapi.component.storage.EnergyStorageProvider;
import com.mrpup.clumapi.component.tank.CombinedFluidHandler;
import com.mrpup.clumapi.component.tank.FluidTankComponent;
import com.mrpup.clumapi.component.tank.InputOnlyFluidHandler;
import com.mrpup.clumapi.component.tank.OutputOnlyFluidHandler;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegComponentCapabilities {

    @SuppressWarnings("unchecked")
    public static void registerAll(RegisterCapabilitiesEvent event) {
        for (var holder : RegBlocks.COMPONENT_BE_TYPES) {
            event.registerBlockEntity(
                    Capabilities.Item.BLOCK,
                    (BlockEntityType<ComponentBlockEntity>) holder.get(),
                    (be, side) -> be.getItemHandler()
            );
        }
        for (var beTypeHolder : RegBlocks.COMPONENT_BE_TYPES) {
            registerEnergyCapabilityIfSupported(event, beTypeHolder.get());
            registerFluidCapabilityIfSupported(event, beTypeHolder.get());
        }
    }

    @SuppressWarnings("unchecked")
    private static void registerEnergyCapabilityIfSupported(
            RegisterCapabilitiesEvent event,
            BlockEntityType<?> type) {

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                (BlockEntityType<BlockEntity>) type,
                (be, side) -> be instanceof EnergyStorageProvider provider
                        ? provider.getEnergyStorage()
                        : null
        );
    }

    @SuppressWarnings("unchecked")
    private static void registerFluidCapabilityIfSupported(
            RegisterCapabilitiesEvent event,
            BlockEntityType<?> type) {

        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                (BlockEntityType<BlockEntity>) type,
                (be, side) -> findTanks(be)
        );
    }

    private static ResourceHandler<FluidResource> findTanks(BlockEntity be) {
        if (!(be instanceof ComponentBlockEntity cbe)) {
            return null;
        }

        List<ResourceHandler<FluidResource>> handlers = new ArrayList<>();
        for (IGuiComponent component : cbe.getComponents()) {
            if (component instanceof FluidTankComponent tankComponent) {
                handlers.add(tankComponent.getHandler());
            }
        }

        if (handlers.isEmpty()) return null;
        if (handlers.size() == 1) return handlers.get(0);
        return new CombinedFluidHandler(handlers);
    }
}
