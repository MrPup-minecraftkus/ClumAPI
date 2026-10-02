package com.mrpup.clumapi.component.bg;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class BGTypes {

    public static final Identifier BG_1 =
            Identifier.fromNamespaceAndPath("clumapi", "textures/gui/background.png");

    private static final int TEX = 256;

    public record BGTexture(Identifier texture, int u, int v, int width, int height) {
        public void render(GuiGraphicsExtractor g, int x, int y) {
            g.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, TEX, TEX);
        }
    }

    public enum BGType {

        BG(new BGTexture(BGTypes.BG_1, 0, 0, 176, 184));

        private final BGTexture bgTexture;


        BGType(BGTexture slotTexture) {
            this.bgTexture = slotTexture;
        }

        public BGTexture getSlotTexture() {
            return bgTexture;
        }

        public Identifier getTexture() {
            return bgTexture.texture();
        }

        public void render(GuiGraphicsExtractor g, int x, int y) {
            bgTexture.render(g, x, y);
        }
    }
}
