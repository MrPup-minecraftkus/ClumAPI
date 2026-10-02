package com.mrpup.clumapi.datagen;

import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataHelper {

    public static void registerApiDataGenClient(GatherDataEvent.Client event, String modId) {
        event.createProvider(output -> new ModelDataGen(output, modId));
        event.addProvider(new RecipesDataGen.Runner(event.getGenerator().getPackOutput(), event.getLookupProvider(), modId));
    }
}