package com.mrpup.clumapi.renders;

/*
public class RegRenderers {

    static final List<Supplier<? extends BlockEntityType<? extends ComponentBlockEntity>>> PENDING = new ArrayList<>();

    public static void reg(Supplier<? extends BlockEntityType<? extends ComponentBlockEntity>> typeSupplier) {
        PENDING.add(typeSupplier);
    }

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (Supplier<? extends BlockEntityType<? extends ComponentBlockEntity>> typeSupplier : PENDING) {
            BlockEntityType<? extends ComponentBlockEntity> type = typeSupplier.get();

            event.registerBlockEntityRenderer(
                    (BlockEntityType<ComponentBlockEntity>) type,
                    ComponentBlockEntityRenderer::new
            );
        }
    }
}

 */