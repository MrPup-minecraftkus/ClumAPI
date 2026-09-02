package com.mrpup.clumapi.component.lock;

import com.mrpup.clumapi.component.IGuiComponent;
import com.mrpup.clumapi.menus.BaseComponentMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class LockComponent implements IGuiComponent {

    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;

    private final int xPos;
    private final int yPos;
    private final ILockable tile;
    private final int toggleButtonId;
    private final int methodButtonId;

    public LockComponent(int xPos, int yPos, ILockable tile, int toggleButtonId, int methodButtonId) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.tile = tile;
        this.toggleButtonId = toggleButtonId;
        this.methodButtonId = methodButtonId;
    }


    @Override
    public void addSlots(BaseComponentMenu menu) {

    }

    @Override
    public void render(GuiGraphics graphics, int leftPos, int topPos) {
        int x = leftPos + xPos;
        int y = topPos + yPos;

        int mode = tile.getLockMode();

        if (mode == 0) {
            LockTypes.LockType.UNLOCK.render(graphics, x, y);
        } else if (mode == 1) {
            LockTypes.LockType.LOCK.render(graphics, x, y);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int leftPos, int topPos) {
        int x = leftPos + xPos;
        int y = topPos + yPos;

        boolean isInside = mouseX >= x && mouseX < x + WIDTH
                && mouseY >= y && mouseY < y + HEIGHT;

        if (!isInside) {
            return false;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.player.containerMenu == null) {
            return false;
        }

        int containerId = mc.player.containerMenu.containerId;

        if (button == 0) {
            mc.gameMode.handleInventoryButtonClick(containerId, toggleButtonId);
            return true;
        } else if (button == 1) {
            mc.gameMode.handleInventoryButtonClick(containerId, methodButtonId);
            return true;
        }

        return false;
    }

    public String getTooltipMode() {
        if (tile.getLockMode() == 0) {
            return "UNLOCK";
        } else if (tile.getLockMode() == 1) {
            return "LOCK";
        }
        return null;
    }

    public String getTooltipMethod() {
        if (tile.getLockMethod() == 0) {
            return "NICKNAME";
        } else if (tile.getLockMethod() == 1) {
            return "UUID";
        }
        return null;
    }

    @Override
    public List<Component> getTooltipLines() {
        return List.of(
                Component.literal(
                        ChatFormatting.WHITE + "Lock Mode: " +
                                ChatFormatting.YELLOW + getTooltipMode()),

                Component.literal(
                        ChatFormatting.WHITE + "Lock Method: " +
                                ChatFormatting.AQUA + getTooltipMethod())
        );
    }

    @Override
    public int[] getButtonIds() {
        return new int[]{toggleButtonId, methodButtonId};
    }

    @Override
    public void onButtonClicked(Player player, int id) {
        if (id == toggleButtonId) {
            tile.cycleLockMode(player);
        } else if (id == methodButtonId) {
            tile.cycleLockMethod();
        }
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
