package com.mrpup.clumapi.component.progress;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ProgressTypes {

    public static final ResourceLocation BG =
            ResourceLocation.fromNamespaceAndPath("clumapi", "textures/gui/background.png");

    private static final int TEX = 256;

    public record ProgressTexture(ResourceLocation texture, int u, int v, int width, int height) {

        public void render(GuiGraphics g, int x, int y) {
            g.blit(texture, x, y, u, v, width, height, TEX, TEX);
        }

        public void renderPartialHorizontal(GuiGraphics g, int x, int y, float filledRatio) {
            filledRatio = Math.max(0f, Math.min(1f, filledRatio));
            int filledWidth = Math.round(width * filledRatio);
            if (filledWidth <= 0) return;

            g.blit(texture, x, y, u, v, filledWidth, height, TEX, TEX);
        }
    }

    public enum ProgressType {

        ARROW(new ProgressTexture(ProgressTypes.BG, 177, 61, 22, 15)),

        ARROW_FULL(new ProgressTexture(ProgressTypes.BG, 177, 77, 22, 15)),

        LONG_ARROW(new ProgressTexture(ProgressTypes.BG, 200, 61, 54, 15)),

        LONG_ARROW_FULL(new ProgressTexture(ProgressTypes.BG, 200, 77, 54, 15));

        private final ProgressTexture texture;

        ProgressType(ProgressTexture texture) {
            this.texture = texture;
        }

        public ProgressTexture getTexture() {
            return texture;
        }

        public void render(GuiGraphics g, int x, int y) {
            texture.render(g, x, y);
        }

        public ProgressType getFullVariant() {
            return switch (this) {
                case ARROW -> ARROW_FULL;
                case LONG_ARROW -> LONG_ARROW_FULL;
                default -> throw new IllegalStateException(
                        "getFullVariant() called on a full variant itself: " + this);
            };
        }
    }
}
