package com.mrpup.clumapi.component.tank;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class FluidTankTypes {

    public static final Identifier BG =
            Identifier.fromNamespaceAndPath("clumapi", "textures/gui/background.png");

    private static final int TEX = 256;

    public record TankTexture(Identifier texture, int u, int v, int width, int height) {
        public void render(GuiGraphicsExtractor g, int x, int y) {
            g.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, TEX, TEX);
        }
    }

    public enum TankType {
        TANK_SMALL(new TankTexture(FluidTankTypes.BG, 196, 1, 18, 19)),

        TANK(new TankTexture(FluidTankTypes.BG, 177, 1, 18, 56));

        private final TankTexture texture;

        TankType(TankTexture texture) {
            this.texture = texture;
        }

        public TankTexture getTexture() {
            return texture;
        }

        public void render(GuiGraphicsExtractor g, int x, int y) {
            texture.render(g, x, y);
        }
    }
}
