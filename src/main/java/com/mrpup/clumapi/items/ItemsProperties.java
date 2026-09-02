package com.mrpup.clumapi.items;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootTable;

public class ItemsProperties {

    private final Item.Properties vanillaProps;

    private ItemsProperties(Item.Properties props) {
        this.vanillaProps = props;
    }

    //

    private boolean shouldGenerateModel = true;

    public ItemsProperties skipModelGen() {
        this.shouldGenerateModel = false;
        return this;
    }

    public boolean isShouldGenerateModel() {
        return shouldGenerateModel;
    }

    //

    private boolean hasDesc = false;

    public ItemsProperties desc() {
        this.hasDesc = true;
        return this;
    }

    public boolean hasDesc() {
        return hasDesc;
    }

    //

    private ResourceLocation targetLootTable = null;
    private float chance = 0.1f;

    public ItemsProperties spawnIn(ResourceKey<LootTable> lootTable, float chance) {
        this.targetLootTable = lootTable.location();
        this.chance = chance;
        return this;
    }

    public ResourceLocation getTargetLootTable() { return targetLootTable; }
    public float getChance() { return chance; }

    //

    public static ItemsProperties copy(Item.Properties props) {
        return new ItemsProperties(props);
    }

    public Item.Properties getVanilla() { return vanillaProps; }
}
