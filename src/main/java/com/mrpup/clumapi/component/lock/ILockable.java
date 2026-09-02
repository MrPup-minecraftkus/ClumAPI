package com.mrpup.clumapi.component.lock;

import net.minecraft.world.entity.player.Player;

public interface ILockable {
    int getLockMode();
    int getLockMethod();
    void cycleLockMode(Player player);
    void cycleLockMethod();
}
