package com.mrpup.clumapi.plugin.jei;

import mezz.jei.api.IModPlugin;
import net.minecraft.resources.ResourceLocation;

public class ClumAPIJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("clumapi", "jei_plugin");
    }

}
