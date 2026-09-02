package com.mrpup.clumapi.blocks;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class BlocksProperties {

    private final BlockBehaviour.Properties vanillaProps;

    private BlocksProperties(BlockBehaviour.Properties props) {
        this.vanillaProps = props;
    }

    public static BlocksProperties copy(BlockBehaviour.Properties props) {
        return new BlocksProperties(props);
    }

    //Mining tier

    private Tiers tier = Tiers.STONE;

    public BlocksProperties setMineTier(Tiers tier) {
        this.tier = tier;
        this.vanillaProps.requiresCorrectToolForDrops();
        return this;
    }

    public Tiers getTier() { return tier; }

    //Model gen

    private boolean shouldGenerateModel = true;

    public BlocksProperties skipModelGen() {
        this.shouldGenerateModel = false;
        return this;
    }

    public boolean isShouldGenerateModel() {
        return shouldGenerateModel;
    }

    //Model gen

    private boolean shouldGenerateModelItem = true;

    public BlocksProperties skipModelGenItem() {
        this.shouldGenerateModelItem = false;
        return this;
    }

    public boolean isShouldGenerateModelItem() {
        return shouldGenerateModelItem;
    }

    // BlockStates Gen


    private boolean shouldGenerateBlockstates = true;

    public BlocksProperties skipBlockstatesGen() {
        this.shouldGenerateBlockstates = false;
        return this;
    }

    public boolean isShouldGenerateBlockstates() {
        return shouldGenerateBlockstates;
    }

    //ore

    private boolean isOre = false;
    private int countPerChunk = 8;
    private int size = 9;
    private int minY = -64, maxY = 64;


    public BlocksProperties asOre(int count, int size, int minY, int maxY) {
        this.isOre = true;
        this.countPerChunk = count;
        this.size = size;
        this.minY = minY;
        this.maxY = maxY;
        return this;
    }

    public boolean isOre() { return isOre; }
    public int getCount() { return countPerChunk; }
    public int getSize() { return size; }
    public int getMinY() { return minY; }
    public int getMaxY() { return maxY; }

    // Block Loot Gen

    private boolean customLoot = false;
    private Supplier<Item> lootItem;
    private int rollCount = 1;
    private int max = 1;
    private int min = 1;

    public BlocksProperties customLoot(Supplier<Item> itemSupplier, int rollCount, int minCount, int maxCount) {
        this.lootItem = itemSupplier;
        this.customLoot = true;
        this.rollCount = rollCount;
        this.max = maxCount;
        this.min = minCount;
        return this;
    }

    public boolean isCustomLoot() {
        return customLoot;
    }

    public Supplier<Item> getItem() {
        return lootItem;
    }

    public int getMax() {
        return max;
    }

    public int getMin()
    {
        return min;
    }

    public int getRollCount() {
        return rollCount;
    }

    public BlockBehaviour.Properties getVanilla() {
        return vanillaProps;
    }
}
