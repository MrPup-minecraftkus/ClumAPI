package com.mrpup.clumapi.fluids;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

public class FluidsHolder {

    private final String name;

    private DeferredHolder<FluidType, FluidType> fluidType;
    private DeferredHolder<Fluid, BaseFlowingFluid.Source> source;
    private DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing;
    private DeferredBlock<LiquidBlock> block;
    private DeferredItem<BucketItem> bucket;

    public FluidsHolder(String name) {
        this.name = name;
    }

    void fill(DeferredHolder<FluidType, FluidType> fluidType,
              DeferredHolder<Fluid, BaseFlowingFluid.Source> source,
              DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing,
              DeferredBlock<LiquidBlock> block,
              DeferredItem<BucketItem> bucket) {
        this.fluidType = fluidType;
        this.source = source;
        this.flowing = flowing;
        this.block = block;
        this.bucket = bucket;
    }

    public String getName() {
        return name;
    }

    public FluidType getFluidType() {
        return fluidType.get();
    }

    public BaseFlowingFluid.Source getSource() {
        return source.get();
    }

    public BaseFlowingFluid.Flowing getFlowing() {
        return flowing.get();
    }

    public LiquidBlock getBlock() {
        return block.get();
    }

    public BucketItem getBucket() {
        return bucket.get();
    }

    public DeferredHolder<FluidType, FluidType> fluidTypeHolder() {
        return fluidType;
    }

    public DeferredHolder<Fluid, BaseFlowingFluid.Source> sourceHolder() {
        return source;
    }

    public DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowingHolder() {
        return flowing;
    }

    public DeferredBlock<LiquidBlock> blockHolder() {
        return block;
    }

    public DeferredItem<BucketItem> bucketHolder() {
        return bucket;
    }
}
