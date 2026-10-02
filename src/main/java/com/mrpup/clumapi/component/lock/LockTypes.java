package com.mrpup.clumapi.component.lock;


import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class LockTypes {
    public static final Identifier BG =
            Identifier.fromNamespaceAndPath("clumapi", "textures/gui/background.png");

    private static final int TEX = 256;

    public record LockTexture(Identifier texture, int u, int v, int width, int height) {
        public void render(GuiGraphicsExtractor g, int x, int y) {
            g.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, TEX, TEX);
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

        public void render(GuiGraphicsExtractor g, int x, int y) {
            texture.render(g, x, y);
        }
    }
}
