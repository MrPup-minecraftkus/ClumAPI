package com.mrpup.clumapi.screens;

import com.mrpup.clumapi.component.IGuiComponent;
import com.mrpup.clumapi.menus.ComponentMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ComponentScreen extends AbstractContainerScreen<ComponentMenu> {
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 184;

    public ComponentScreen(ComponentMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (IGuiComponent component : menu.getBlockEntity().getComponents()) {
            if (component.mouseClicked(mouseX, mouseY, button, leftPos, topPos)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component title = this.title;

        int titleWidth = this.font.width(title);
        int centerX = (this.imageWidth / 2) - (titleWidth / 2);

        guiGraphics.drawString(this.font, title, centerX, 6, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pTicks, int mouseX, int mouseY) {
        menu.getBlockEntity().getBgType().render(graphics, leftPos, topPos);

        for (IGuiComponent component : menu.getBlockEntity().getComponents()) {
            component.render(graphics, leftPos, topPos);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float pTicks) {
        this.renderBackground(graphics, mouseX, mouseY, pTicks);
        super.render(graphics, mouseX, mouseY, pTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        for (IGuiComponent component : menu.getBlockEntity().getComponents()) {
            if (component.isHovered(mouseX, mouseY, leftPos, topPos) && !component.getTooltipLines().isEmpty()) {
                graphics.renderComponentTooltip(this.font, component.getTooltipLines(), mouseX, mouseY);
            }
        }
    }
}