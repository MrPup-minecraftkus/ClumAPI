package com.mrpup.clumapi.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataHelper {

    public static void registerApiDataGen(GatherDataEvent event, String modId) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new BlockStateDataGen(output, modId, helper));
        generator.addProvider(event.includeClient(), new ItemsDataGen(output, modId, helper));

        generator.addProvider(event.includeServer(), new RecipesDataGen(output, event.getLookupProvider(), modId));
    }
}
