package com.mrpup.clumapi.component.redstone;

import com.mrpup.clumapi.component.IGuiComponent;
import com.mrpup.clumapi.menus.BaseComponentMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class RedstoneComponent implements IGuiComponent {

    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;

    private final int xPos;
    private final int yPos;
    private final IRedstoneControllable tile;
    private final int buttonId;

    public RedstoneComponent(int xPos, int yPos, IRedstoneControllable tile, int buttonId) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.tile = tile;
        this.buttonId = buttonId;
    }


    @Override
    public void addSlots(BaseComponentMenu menu) {

    }

    @Override
    public void render(GuiGraphics graphics, int leftPos, int topPos) {
        int x = leftPos + xPos;
        int y = topPos + yPos;

        int mode = tile.getRedstoneMode();

        if (mode == 0) {
            RedstoneTypes.RedstoneType.REDSTONE_OFF.render(graphics, x, y);
        } else if (mode == 1) {
            RedstoneTypes.RedstoneType.REDSTONE_ON.render(graphics, x, y);
        } else if (mode == 2) {
            RedstoneTypes.RedstoneType.REDSTONE_REVERSE.render(graphics, x, y);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int leftPos, int topPos) {
        int x = leftPos + xPos;
        int y = topPos + yPos;

        boolean isInside = mouseX >= x && mouseX < x + WIDTH
                && mouseY >= y && mouseY < y + HEIGHT;

        if (isInside && button == 0) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.containerMenu != null) {
                mc.gameMode.handleInventoryButtonClick(
                        mc.player.containerMenu.containerId,
                        buttonId
                );
            }
            return true;
        }

        return false;
    }

    public String getTooltip() {
        if (tile.getRedstoneMode() == 0) {
            return "IGNORED";
        } else if (tile.getRedstoneMode() == 1) {
            return "NEEDED";
        } else if (tile.getRedstoneMode() == 2) {
            return "REVERSED";
        }
        return null;
    }

    @Override
    public List<Component> getTooltipLines() {
        return List.of(
                Component.literal(
                        ChatFormatting.WHITE + "Redstone Mode: " +
                                ChatFormatting.RED + getTooltip())
        );
    }

    @Override
    public int getButtonId() {
        return buttonId;
    }

    @Override
    public void onButtonClicked(Player player, int buttonId) {
        tile.cycleRedstoneMode();
    }


    @Override
    public int getX() {
        return xPos;
    }

    @Override
    public int getY() {
        return yPos;
    }

    @Override
    public boolean isHovered(int mouseX, int mouseY, int leftPos, int topPos) {
        int x = leftPos + xPos;
        int y = topPos + yPos;
        return mouseX >= x && mouseX < x + WIDTH && mouseY >= y && mouseY < y + HEIGHT;
    }

}
