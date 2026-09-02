package com.mrpup.clumapi.component;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface ISerializableComponent {

    void saveComponent(CompoundTag tag, HolderLookup.Provider registries);

    void loadComponent(CompoundTag tag, HolderLookup.Provider registries);

    String getSaveKey();
}