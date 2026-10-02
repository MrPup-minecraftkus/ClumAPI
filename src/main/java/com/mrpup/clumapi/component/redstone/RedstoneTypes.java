package com.mrpup.clumapi.component.redstone;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class RedstoneTypes {
    public static final Identifier BG =
            Identifier.fromNamespaceAndPath("clumapi", "textures/gui/background.png");

    private static final int TEX = 256;

    public record RedstoneTexture(Identifier texture, int u, int v, int width, int height) {
        public void render(GuiGraphicsExtractor g, int x, int y) {
            g.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, TEX, TEX);
        }
    }

    public enum RedstoneType {

        REDSTONE_OFF(new RedstoneTexture(RedstoneTypes.BG, 167, 216, 20, 20)),
        REDSTONE_ON(new RedstoneTexture(RedstoneTypes.BG, 146, 216, 20, 20)),
        REDSTONE_REVERSE(new RedstoneTexture(RedstoneTypes.BG, 188, 216, 20, 20));


        private final RedstoneTexture texture;

        RedstoneType(RedstoneTexture texture) {
            this.texture = texture;
        }

        public RedstoneTexture getTexture() {
            return texture;
        }

        public void render(GuiGraphicsExtractor g, int x, int y) {
            texture.render(g, x, y);
        }
    }
}
