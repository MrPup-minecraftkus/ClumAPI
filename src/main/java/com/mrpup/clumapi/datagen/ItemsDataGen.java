package com.mrpup.clumapi.datagen;

import com.mrpup.clumapi.blocks.RegBlocks;
import com.mrpup.clumapi.items.RegItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemsDataGen extends ItemModelProvider {
    public ItemsDataGen(PackOutput output, String modId, ExistingFileHelper existingFileHelper) {
        super(output, modId, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        RegBlocks.BLOCK_STORAGE.forEach((name, entry) -> {
            if (!entry.props().isShouldGenerateModelItem()) return;
            withExistingParent(name, modLoc("block/" + name));
        });

        RegItems.ITEMS_STORAGE.forEach((name, entry) -> {
            withExistingParent(name, mcLoc("item/generated")).texture("layer0", modLoc("item/" + name));
        });
    }
}
