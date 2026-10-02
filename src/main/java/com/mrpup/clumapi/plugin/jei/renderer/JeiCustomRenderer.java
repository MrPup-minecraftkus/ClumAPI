package com.mrpup.clumapi.plugin.jei.renderer;

import com.mrpup.clumapi.component.slot.SlotTypes;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class JeiCustomRenderer {

    public static void multiSlot(int x, int y, int cols, int rows, SlotTypes.SlotType slotType, GuiGraphicsExtractor guiGraphics) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                int slotX = x + col * 18;
                int slotY = y + row * 18;

                slotType.render(guiGraphics, slotX, slotY);
            }
        }
    }
}
