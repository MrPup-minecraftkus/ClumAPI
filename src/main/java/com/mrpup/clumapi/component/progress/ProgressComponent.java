package com.mrpup.clumapi.component.progress;

import com.mrpup.clumapi.component.IGuiComponent;
import com.mrpup.clumapi.menus.BaseComponentMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.DataSlot;

import java.util.List;

public class ProgressComponent implements IGuiComponent {

    private final ProgressTypes.ProgressType progressType;
    private final int width;
    private final int height;

    private final int xPos;
    private final int yPos;
    private int progress;
    private int maxProgress;

    public ProgressComponent(int xPos, int yPos, int maxProgress) {
        this(xPos, yPos, maxProgress, ProgressTypes.ProgressType.ARROW);
    }

    public ProgressComponent(int xPos, int yPos, int maxProgress, ProgressTypes.ProgressType progressType) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.maxProgress = Math.max(1, maxProgress);
        this.progressType = progressType;
        this.width = progressType.getTexture().width();
        this.height = progressType.getTexture().height();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(maxProgress, progress));
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = Math.max(1, maxProgress);
    }

    @Override
    public void addSlots(BaseComponentMenu menu) {
    }

    @Override
    public void render(GuiGraphics graphics, int leftPos, int topPos) {
        int x = leftPos + xPos;
        int y = topPos + yPos;

        progressType.render(graphics, x, y);

        float ratio = (float) progress / maxProgress;
        progressType.getFullVariant().getTexture().renderPartialHorizontal(graphics, x, y, ratio);
    }

    @Override
    public int getX() {
        return xPos;
    }

    @Override
    public int getY() {
        return yPos;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ProgressTypes.ProgressType getProgressType() {
        return progressType;
    }

    @Override
    public List<DataSlot> getDataSlots() {
        return List.of(
                new DataSlot() {
                    @Override public int get() { return progress; }
                    @Override public void set(int value) { progress = value; }
                },
                new DataSlot() {
                    @Override public int get() { return maxProgress; }
                    @Override public void set(int value) { maxProgress = Math.max(1, value); }
                }
        );
    }

    @Override
    public List<Component> getTooltipLines() {
        return List.of(
                Component.literal(ChatFormatting.GOLD + "Progress:"),
                Component.literal(
                        ChatFormatting.WHITE + String.valueOf(progress)
                                + ChatFormatting.GRAY + " / "
                                + ChatFormatting.WHITE + maxProgress)
        );
    }

    @Override
    public boolean isHovered(int mouseX, int mouseY, int leftPos, int topPos) {
        int x = leftPos + xPos;
        int y = topPos + yPos;
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
