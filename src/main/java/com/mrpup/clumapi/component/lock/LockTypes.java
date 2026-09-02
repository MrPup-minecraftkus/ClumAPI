package com.mrpup.clumapi.component.lock;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class LockTypes {
    public static final ResourceLocation BG =
            ResourceLocation.fromNamespaceAndPath("clumapi", "textures/gui/background.png");

    private static final int TEX = 256;

    public record LockTexture(ResourceLocation texture, int u, int v, int width, int height) {
        public void render(GuiGraphics g, int x, int y) {
            g.blit(texture, x, y, u, v, width, height, TEX, TEX);
        }
    }

    public enum LockType {

        LOCK(new LockTexture(LockTypes.BG, 89, 208, 27, 28)),
        UNLOCK(new LockTexture(LockTypes.BG, 117, 208, 27, 28));


        private final LockTexture texture;

        LockType(LockTexture texture) {
            this.texture = texture;
        }

        public LockTexture getTexture() {
            return texture;
        }

        public void render(GuiGraphics g, int x, int y) {
            texture.render(g, x, y);
        }
    }
}
