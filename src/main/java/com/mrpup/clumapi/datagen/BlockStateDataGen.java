package com.mrpup.clumapi.datagen;

import com.mrpup.clumapi.blocks.RegBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockStateDataGen extends BlockStateProvider {
    public BlockStateDataGen(PackOutput output, String modid , ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (var entry : RegBlocks.BLOCK_STORAGE.entrySet()) {
            String name = entry.getKey();
            var blockEntry = entry.getValue();
            Block block = blockEntry.block().get();

            if (block.getStateDefinition().getProperties().contains(HorizontalDirectionalBlock.FACING)) {
                var blockModel = models().getExistingFile(modLoc("block/" + name));
                if (!blockEntry.props().isShouldGenerateBlockstates()) continue;

                getVariantBuilder(block)
                        .partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                        .modelForState().modelFile(blockModel).addModel()
                        .partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
                        .modelForState().modelFile(blockModel).rotationY(180).addModel()
                        .partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
                        .modelForState().modelFile(blockModel).rotationY(90).addModel()
                        .partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)
                        .modelForState().modelFile(blockModel).rotationY(270).addModel();

            } else {
                simpleBlock(block);
            }
        }
    }
}
