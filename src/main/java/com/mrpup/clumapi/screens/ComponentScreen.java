package com.mrpup.clumapi.screens;

import com.mrpup.clumapi.component.IGuiComponent;
import com.mrpup.clumapi.menus.ComponentMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class ComponentScreen extends AbstractContainerScreen<ComponentMenu> {
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 184;

    public ComponentScreen(ComponentMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, GUI_WIDTH, GUI_HEIGHT);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        for (IGuiComponent component : menu.getBlockEntity().getComponents()) {
            if (component.mouseClicked(event.x(), event.y(), event.button(), leftPos, topPos)) {
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        Component title = this.title;
        int titleWidth = this.font.width(title);
        int centerX = (this.imageWidth / 2) - (titleWidth / 2);
        guiGraphics.text(this.font, title, centerX, 6, ARGB.opaque(4210752), false);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        menu.getBlockEntity().getBgType().render(graphics, leftPos, topPos);

        for (IGuiComponent component : menu.getBlockEntity().getComponents()) {
            component.render(graphics, leftPos, topPos);
        }

        super.extractContents(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        for (IGuiComponent component : menu.getBlockEntity().getComponents()) {
            if (component.isHovered(mouseX, mouseY, leftPos, topPos) && !component.getTooltipLines().isEmpty()) {
                graphics.setTooltipForNextFrame(this.font, component.getTooltipLines(), Optional.empty(), mouseX, mouseY);
            }
        }
    }
}