package com.mrpup.clumapi.component;

import com.mrpup.clumapi.blocks.RegBlocks;
import com.mrpup.clumapi.blocks.entity.ComponentBlockEntity;
import com.mrpup.clumapi.component.storage.EnergyStorageProvider;
import com.mrpup.clumapi.component.tank.FluidTankComponent;
import com.mrpup.clumapi.component.tank.InputOnlyFluidHandler;
import com.mrpup.clumapi.component.tank.OutputOnlyFluidHandler;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Objects;

public class RegComponentCapabilities {

    @SuppressWarnings("unchecked")
    public static void registerAll(RegisterCapabilitiesEvent event) {
        for (var holder : RegBlocks.COMPONENT_BE_TYPES) {
            event.registerBlockEntity(
                    Capabilities.ItemHandler.BLOCK,
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
                Capabilities.EnergyStorage.BLOCK,
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
                Capabilities.FluidHandler.BLOCK,
                (BlockEntityType<BlockEntity>) type,
                (be, side) -> findFirstTank(be)
        );
    }

    private static IFluidHandler findFirstTank(BlockEntity be) {
        if (!(be instanceof ComponentBlockEntity cbe)) {
            return null;
        }

        for (IGuiComponent component : cbe.getComponents()) {
            if (component instanceof FluidTankComponent tankComponent) {
                if (Objects.equals(tankComponent.getSaveKey(), "output_tank")) {
                    return new OutputOnlyFluidHandler(tankComponent.getTank());
                } else if (Objects.equals(tankComponent.getSaveKey(), "input_tank")) {
                    return new InputOnlyFluidHandler(tankComponent.getTank());
                } else {
                    return tankComponent.getTank();
                }
            }
        }

        return null;
    }
}
