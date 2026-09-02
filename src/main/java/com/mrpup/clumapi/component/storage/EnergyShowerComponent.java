package com.mrpup.clumapi.component.storage;

import com.mrpup.clumapi.component.IGuiComponent;
import com.mrpup.clumapi.menus.BaseComponentMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.text.DecimalFormat;
import java.util.List;

public class EnergyShowerComponent implements IGuiComponent {
    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;

    private final int xPos;
    private final int yPos;
    private final IEnergy tile;

    public EnergyShowerComponent(int xPos, int yPos, IEnergy tile) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.tile = tile;
    }


    @Override
    public void addSlots(BaseComponentMenu menu) {

    }

    @Override
    public void render(GuiGraphics graphics, int leftPos, int topPos) {
        int x = leftPos + xPos;
        int y = topPos + yPos;

        StorageTypes.StorageType.ENERGY_SHOWER.render(graphics, x, y);
    }

    @Override
    public List<Component> getTooltipLines() {
        DecimalFormat fmt = new DecimalFormat("#,###");

        int energyCost;

        if (tile.getSpeedMultiplier() == 1) {
            energyCost = tile.getEffectiveEnergyCost(tile.getPerTickEnergy());
        } else {
            energyCost = tile.getEffectiveEnergyCost(tile.getPerTickEnergy())  + ( 10 * (int) tile.getSpeedMultiplier());
        }
        return List.of(
                Component.literal(
                        ChatFormatting.GREEN + "Stored: " +
                        ChatFormatting.WHITE + fmt.format(tile.getEnergy())
                                + ChatFormatting.GRAY + " / "
                                + ChatFormatting.WHITE + fmt.format(tile.getCapacity())
                                + ChatFormatting.DARK_AQUA + " FE"),

                Component.literal(
                        ChatFormatting.YELLOW + "Consumed: "
                                + ChatFormatting.WHITE + energyCost
                                + ChatFormatting.DARK_AQUA + " FE"
                                + ChatFormatting.GRAY + "/"
                                + ChatFormatting.RED + "t")
        );
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
