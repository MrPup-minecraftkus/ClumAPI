package com.mrpup.clumapi.component.tank;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.List;

public class CombinedFluidHandler implements ResourceHandler<FluidResource> {

    private final List<ResourceHandler<FluidResource>> handlers;
    private final int[] offsets;
    private final int totalSize;

    public CombinedFluidHandler(List<ResourceHandler<FluidResource>> handlers) {
        this.handlers = List.copyOf(handlers);
        this.offsets = new int[handlers.size()];

        int sum = 0;
        for (int i = 0; i < handlers.size(); i++) {
            offsets[i] = sum;
            sum += handlers.get(i).size();
        }
        this.totalSize = sum;
    }

    private int handlerFor(int index) {
        if (index < 0 || index >= totalSize) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + totalSize);
        }
        for (int i = handlers.size() - 1; i >= 0; i--) {
            if (index >= offsets[i]) return i;
        }
        throw new IllegalStateException();
    }

    @Override
    public int size() {
        return totalSize;
    }

    @Override
    public FluidResource getResource(int index) {
        int h = handlerFor(index);
        return handlers.get(h).getResource(index - offsets[h]);
    }

    @Override
    public long getAmountAsLong(int index) {
        int h = handlerFor(index);
        return handlers.get(h).getAmountAsLong(index - offsets[h]);
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        int h = handlerFor(index);
        return handlers.get(h).getCapacityAsLong(index - offsets[h], resource);
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        int h = handlerFor(index);
        return handlers.get(h).isValid(index - offsets[h], resource);
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        int h = handlerFor(index);
        return handlers.get(h).insert(index - offsets[h], resource, amount, transaction);
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        int h = handlerFor(index);
        return handlers.get(h).extract(index - offsets[h], resource, amount, transaction);
    }
}
