package com.mrpup.clumapi.component.slot;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class SlotTypes {

    public static final Identifier BG =
            Identifier.fromNamespaceAndPath("clumapi", "textures/gui/background.png");

    private static final int TEX = 256;


    public record SlotTexture(Identifier texture, int u, int v, int width, int height) {
        public void render(GuiGraphicsExtractor g, int x, int y) {
            if (width == 18 && height == 18) {
                g.blit(RenderPipelines.GUI_TEXTURED, texture, x - 1, y - 1, u, v, width, height, TEX, TEX);
            } else if (width == 26 && height == 26) {
                g.blit(RenderPipelines.GUI_TEXTURED, texture, x - 5, y - 5, u, v, width, height, TEX, TEX);
            } else {
                g.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, TEX, TEX);
            }
        }
    }

    public interface SlotType {
        boolean accepts(ItemStack stack);

        SlotTexture getSlotTexture();

        default Identifier getTexture() {
            return getSlotTexture().texture();
        }

        default float[] getColor() {
            return new float[]{1f, 1f, 1f};
        }

        default int getMaxStackSize() {
            return 64;
        }

        default SlotType getOutputVariant() {
            return SlotTypes.OUTPUT_SLOT_ITEM;
        }

        default void render(GuiGraphicsExtractor g, int x, int y) {
            float[] c = getColor();

            int r = Math.round(c[0] * 255f);
            int gC = Math.round(c[1] * 255f);
            int b = Math.round(c[2] * 255f);

            int outerColor = (130 << 24) | (r << 16) | (gC << 8) | b;
            int innerColor = (200 << 24) | (r << 16) | (gC << 8) | b;

            int w = getSlotTexture().width();
            int h = getSlotTexture().height();
            int offsetX = (w - 16) / 2;
            int offsetY = (h - 16) / 2;

            int left = x - offsetX;
            int top = y - offsetY;

            g.fill(left - 1, top - 1, left + w + 1, top, outerColor);
            g.fill(left - 1, top + h, left + w + 1, top + h + 1, outerColor);
            g.fill(left - 1, top, left, top + h, outerColor);
            g.fill(left + w, top, left + w + 1, top + h, outerColor);
            g.fill(left + 1, top + 1, left + w - 1, top + h - 1, innerColor);

            getSlotTexture().render(g, x, y);
        }
    }

    public static final SlotType SLOT_ITEM = new SlotType() {
        private final SlotTexture texture = new SlotTexture(SlotTypes.BG, 1, 185, 18, 18);

        @Override
        public boolean accepts(ItemStack stack) {
            return true;
        }

        @Override
        public SlotTexture getSlotTexture() {
            return texture;
        }

        @Override
        public float[] getColor() {
            int colorInt = DyeColor.LIGHT_BLUE.getFireworkColor();

            float r = ((colorInt >> 16) & 0xFF) / 255.0f;
            float g = ((colorInt >> 8) & 0xFF) / 255.0f;
            float b = (colorInt & 0xFF) / 255.0f;

            return new float[]{r, g, b};
        }
    };

    public static final SlotType OUTPUT_SLOT_ITEM = new SlotType() {
        private final SlotTexture texture = new SlotTexture(SlotTypes.BG, 1, 185, 18, 18);

        @Override
        public boolean accepts(ItemStack stack) {
            return true;
        }

        @Override
        public SlotTexture getSlotTexture() {
            return texture;
        }

        @Override
        public SlotType getOutputVariant() {
            return this;
        }

        @Override
        public float[] getColor() {
            int colorInt = DyeColor.ORANGE.getFireworkColor();

            float r = ((colorInt >> 16) & 0xFF) / 255.0f;
            float g = ((colorInt >> 8) & 0xFF) / 255.0f;
            float b = (colorInt & 0xFF) / 255.0f;

            return new float[]{r, g, b};
        }
    };

    public static final SlotType BIG_SLOT_ITEM = new SlotType() {
        private final SlotTexture texture = new SlotTexture(SlotTypes.BG, 1, 223, 26, 26);

        @Override
        public boolean accepts(ItemStack stack) {
            return true;
        }

        @Override
        public SlotTexture getSlotTexture() {
            return texture;
        }

        @Override
        public SlotType getOutputVariant() {
            return this;
        }

        @Override
        public float[] getColor() {
            int colorInt = DyeColor.ORANGE.getFireworkColor();

            float r = ((colorInt >> 16) & 0xFF) / 255.0f;
            float g = ((colorInt >> 8) & 0xFF) / 255.0f;
            float b = (colorInt & 0xFF) / 255.0f;

            return new float[]{r, g, b};
        }
    };
}