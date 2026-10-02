package com.mrpup.clumapi.fluids;

import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.fluid.FluidTintSource;

public record SimpleFluidTint(int color) implements FluidTintSource {
    @Override
    public int color(FluidState state) {
        return color;
    }
}
