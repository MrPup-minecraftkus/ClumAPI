package com.mrpup.clumapi.component.bg;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class BGTypes {

    public static final ResourceLocation BG_1 =
            ResourceLocation.fromNamespaceAndPath("clumapi", "textures/gui/background.png");

    private static final int TEX = 256;

    public record BGTexture(ResourceLocation texture, int u, int v, int width, int height) {
        public void render(GuiGraphics g, int x, int y) {
            g.blit(texture, x, y, u, v, width, height, TEX, TEX);
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

        public ResourceLocation getTexture() {
            return bgTexture.texture();
        }

        public void render(GuiGraphics g, int x, int y) {
            bgTexture.render(g, x, y);
        }
    }
}
