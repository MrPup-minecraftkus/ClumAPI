package com.mrpup.clumapi.component.tank;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class InputOnlyFluidHandler implements ResourceHandler<FluidResource> {

    private final ResourceHandler<FluidResource> delegate;

    public InputOnlyFluidHandler(ResourceHandler<FluidResource> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public FluidResource getResource(int index) {
        return delegate.getResource(index);
    }

    @Override
    public long getAmountAsLong(int index) {
        return delegate.getAmountAsLong(index);
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return delegate.getCapacityAsLong(index, resource);
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return delegate.isValid(index, resource);
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        return delegate.insert(index, resource, amount, transaction);
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        return 0;
    }
}