package com.mrpup.clumapi.plugin.jei;

import mezz.jei.api.IModPlugin;
import net.minecraft.resources.Identifier;

public class ClumAPIJeiPlugin implements IModPlugin {

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath("clumapi", "jei_plugin");
    }

}
