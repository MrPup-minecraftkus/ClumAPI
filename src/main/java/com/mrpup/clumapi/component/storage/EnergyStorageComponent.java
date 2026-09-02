package com.mrpup.clumapi.component.storage;

import com.mrpup.clumapi.component.IGuiComponent;
import com.mrpup.clumapi.menus.BaseComponentMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.DataSlot;
import net.neoforged.neoforge.energy.EnergyStorage;

import java.text.DecimalFormat;
import java.util.List;

public class EnergyStorageComponent extends EnergyStorage implements IGuiComponent {

    private static final int WIDTH = StorageTypes.StorageType.ENERGY_STORAGE.getSlotTexture().width();
    private static final int HEIGHT = StorageTypes.StorageType.ENERGY_STORAGE.getSlotTexture().height();

    private final int xPos;
    private final int yPos;

    public EnergyStorageComponent(int maxCapacity, int maxReceive, int maxExtract, int xPos, int yPos) {
        super(maxCapacity, maxReceive, maxExtract);
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public EnergyStorageComponent(int maxCapacity, int maxIO, int xPos, int yPos) {
        this(maxCapacity, maxIO, maxIO, xPos, yPos);
    }

    public void setEnergyStored(int energy) {
        this.energy = Math.max(0, Math.min(getMaxEnergyStored(), energy));
    }

    @Override
    public void addSlots(BaseComponentMenu menu) {

    }

    @Override
    public void render(GuiGraphics graphics, int leftPos, int topPos) {
        int x = leftPos + xPos;
        int y = topPos + yPos;

        StorageTypes.StorageType.ENERGY_STORAGE.render(graphics, x, y);


        float ratio = getMaxEnergyStored() > 0
                ? (float) getEnergyStored() / getMaxEnergyStored()
                : 0f;

        int insetX = (WIDTH - StorageTypes.StorageType.ENERGY_STORAGE_FULL.getSlotTexture().width()) / 2;
        int insetY = (HEIGHT - StorageTypes.StorageType.ENERGY_STORAGE_FULL.getSlotTexture().height()) / 2;

        StorageTypes.StorageType.ENERGY_STORAGE_FULL.getSlotTexture()
                .renderPartial(graphics, x + insetX, y + insetY, ratio);
    }

    @Override
    public int getX() { return xPos; }

    @Override
    public int getY() { return yPos; }

    public int getWidth() { return WIDTH; }
    public int getHeight() { return HEIGHT; }

    @Override
    public List<DataSlot> getDataSlots() {
        return List.of(new DataSlot() {
            @Override public int get() { return getEnergyStored(); }
            @Override public void set(int value) { setEnergyStored(value); }
        });
    }

    @Override
    public List<Component> getTooltipLines() {
        DecimalFormat fmt = new DecimalFormat("#,###");
        return List.of(
                Component.literal(
                        ChatFormatting.GOLD + "Stored: "
                                + ChatFormatting.WHITE + fmt.format(getEnergyStored())
                                + ChatFormatting.GRAY + " / "
                                + ChatFormatting.WHITE + fmt.format(getMaxEnergyStored())
                                + ChatFormatting.DARK_AQUA + " FE")
        );
    }

    @Override
    public boolean isHovered(int mouseX, int mouseY, int leftPos, int topPos) {
        int x = leftPos + xPos;
        int y = topPos + yPos;
        return mouseX >= x && mouseX < x + WIDTH && mouseY >= y && mouseY < y + HEIGHT;
    }
}