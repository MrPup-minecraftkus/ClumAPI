package com.mrpup.clumapi.fluids;

import com.mrpup.clumapi.items.ItemsHolder;
import com.mrpup.clumapi.items.RegItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RegFluids {

    public static final Map<String, FluidsHolder> FLUID_STORAGE = new HashMap<>();
    private static final Map<String, IClientFluidTypeExtensions> CLIENT_EXTENSIONS = new HashMap<>();

    private static DeferredRegister<FluidType> FLUID_TYPES;
    private static DeferredRegister<Fluid> FLUIDS;
    private static DeferredRegister.Blocks BLOCKS;

    public static void init(String modId, IEventBus modEventBus) {
        FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, modId);
        FLUIDS = DeferredRegister.create(Registries.FLUID, modId);
        BLOCKS = DeferredRegister.createBlocks(modId);

        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
        BLOCKS.register(modEventBus);
    }

    public static FluidsHolder reg(String name, Supplier<FluidType.Properties> fluidAttrs, IClientFluidTypeExtensions renderProperties) {

        FluidsHolder holder = new FluidsHolder(name);

        CLIENT_EXTENSIONS.put(name, renderProperties);

        DeferredHolder<FluidType, FluidType> fluidType = FLUID_TYPES.register(name, () ->
                        new FluidType(fluidAttrs.get().sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_EMPTY))
        );

        BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(
                fluidType::get,
                holder::getSource,
                holder::getFlowing
        )
                .slopeFindDistance(2)
                .levelDecreasePerBlock(1)
                .block(holder::getBlock)
                .bucket(holder::getBucket);

        DeferredHolder<Fluid, BaseFlowingFluid.Source> source = FLUIDS.register(name,
                () -> new BaseFlowingFluid.Source(properties));

        DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing = FLUIDS.register(name + "_flowing",
                () -> new BaseFlowingFluid.Flowing(properties));

        DeferredBlock<LiquidBlock> block = BLOCKS.register(name + "_block", () ->
                new LiquidBlock(holder.getSource(), BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WATER)
                        .replaceable()
                        .noCollission()
                        .strength(100.0F)
                        .pushReaction(PushReaction.DESTROY)
                        .noLootTable()
                        .liquid()
                        .sound(SoundType.EMPTY))
        );

        ItemsHolder<BucketItem> bucketHolder = RegItems.reg(name + "_bucket", () ->
                new BucketItem(holder.getSource(), new Item.Properties()
                        .craftRemainder(Items.BUCKET)
                        .stacksTo(1))
        );

        DeferredItem<BucketItem> bucket = bucketHolder.item();

        holder.fill(fluidType, source, flowing, block, bucket);

        FLUID_STORAGE.put(name, holder);
        return holder;
    }

    public static Map<String, IClientFluidTypeExtensions> getClientExtensions() {
        return CLIENT_EXTENSIONS;
    }
}
