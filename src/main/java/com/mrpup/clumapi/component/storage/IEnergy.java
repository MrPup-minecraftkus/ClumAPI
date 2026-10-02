package com.mrpup.clumapi.component.storage;


public interface IEnergy {
    int getEnergy();
    float getEfficiencyMultiplier();
    float getSpeedMultiplier();
    int getEffectiveEnergyCost(int baseCost);
    int getPerTickEnergy();
    int getCapacity();
}

