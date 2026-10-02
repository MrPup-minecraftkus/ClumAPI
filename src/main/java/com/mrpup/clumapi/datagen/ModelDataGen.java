package com.mrpup.clumapi.datagen;

import com.mojang.math.Quadrant;
import com.mrpup.clumapi.blocks.RegBlocks;
import com.mrpup.clumapi.fluids.RegFluids;
import com.mrpup.clumapi.items.RegItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ModelDataGen extends ModelProvider {

    public ModelDataGen(PackOutput output, String modId) {
        super(output, modId);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        registerItemModels(itemModels);
        registerBlockModels(blockModels);
    }

    private void registerBlockModels(BlockModelGenerators blockModels) {
        for (var entry : RegBlocks.BLOCK_STORAGE.entrySet()) {
            String name = entry.getKey();
            var blockEntry = entry.getValue();
            Block block = blockEntry.block().get();


            Identifier modelLoc = this.modLocation("block/" + name);
            Variant baseVariant = new Variant(modelLoc);

            if (block.getStateDefinition().getProperties().contains(HorizontalDirectionalBlock.FACING)) {
                blockModels.blockStateOutput.accept(
                        MultiVariantGenerator.dispatch(block, BlockModelGenerators.variant(baseVariant))
                                .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
                                                .select(Direction.NORTH,  VariantMutator.Y_ROT.withValue(Quadrant.R0))
                                                .select(Direction.SOUTH, VariantMutator.Y_ROT.withValue(Quadrant.R180))
                                                .select(Direction.EAST,VariantMutator.Y_ROT.withValue(Quadrant.R90))
                                                .select(Direction.WEST,VariantMutator.Y_ROT.withValue(Quadrant.R270))
                                )
                );
            } else {
                blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, BlockModelGenerators.variant(baseVariant)));
            }
        }

        for (var entry : RegFluids.FLUID_STORAGE.entrySet()) {
            String name = entry.getKey();
            var blockEntry = entry.getValue();
            Block block = blockEntry.getBlock();


            Identifier modelLoc = this.modLocation("block/" + name);
            Variant baseVariant = new Variant(modelLoc);
            blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, BlockModelGenerators.variant(baseVariant)));
        }
    }

    private void registerItemModels(ItemModelGenerators itemModels) {
        RegItems.ITEMS_STORAGE.forEach((name, entry) -> itemModels.generateFlatItem(entry.item().get(), ModelTemplates.FLAT_ITEM));
    }
}