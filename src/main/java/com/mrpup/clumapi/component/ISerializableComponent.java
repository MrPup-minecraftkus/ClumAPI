package com.mrpup.clumapi.component;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface ISerializableComponent {

    void saveComponent(ValueOutput output);

    void loadComponent(ValueInput input);

    String getSaveKey();
}