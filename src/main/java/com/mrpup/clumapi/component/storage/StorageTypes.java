package com.mrpup.clumapi.component.storage;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class StorageTypes {
    public static final ResourceLocation BG =
            ResourceLocation.fromNamespaceAndPath("clumapi", "textures/gui/background.png");

    private static final int TEX = 256;

    public record StorageTexture(ResourceLocation texture, int u, int v, int width, int height) {

        public void render(GuiGraphics g, int x, int y) {
            g.blit(texture, x, y, u, v, width, height, TEX, TEX);
        }

        public void renderPartial(GuiGraphics g, int x, int y, float filledRatio) {
            filledRatio = Math.max(0f, Math.min(1f, filledRatio));
            int filledHeight = Math.round(height * filledRatio);
            if (filledHeight <= 0) return;

            g.blit(texture,
                    x, y + (height - filledHeight),
                    u, v + (height - filledHeight),
                    width, filledHeight,
                    TEX, TEX);
        }
    }

    public enum StorageType {

        ENERGY_STORAGE(new StorageTexture(StorageTypes.BG, 177, 94, 19, 56)),

        ENERGY_STORAGE_FULL(new StorageTexture(StorageTypes.BG, 197, 97, 13, 50)),

        ENERGY_SHOWER(new StorageTexture(StorageTypes.BG, 209, 216, 20, 20));

        private final StorageTexture storageTexture;

        StorageType(StorageTexture slotTexture) {
            this.storageTexture = slotTexture;
        }

        public StorageTexture getSlotTexture() {
            return storageTexture;
        }

        public ResourceLocation getTexture() {
            return storageTexture.texture();
        }

        public void render(GuiGraphics g, int x, int y) {
            storageTexture.render(g, x, y);
        }
    }
}

