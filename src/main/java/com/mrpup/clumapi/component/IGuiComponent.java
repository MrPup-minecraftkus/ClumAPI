package com.mrpup.clumapi.component;

import com.mrpup.clumapi.component.bg.BGTypes;
import com.mrpup.clumapi.menus.BaseComponentMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public interface IGuiComponent {

    void addSlots(BaseComponentMenu menu);

    @OnlyIn(Dist.CLIENT)
    void render(GuiGraphics graphics, int leftPos, int topPos);

    int getX();
    int getY();

    default BGTypes.BGType getBgType() {
        return BGTypes.BGType.BG;
    }

    default List<DataSlot> getDataSlots() {
        return List.of();
    }

    default List<Component> getTooltipLines() {
        return List.of();
    }

    default boolean isHovered(int mouseX, int mouseY, int leftPos, int topPos) {
        return false;
    }

    default boolean mouseClicked(double mouseX, double mouseY, int button, int leftPos, int topPos) {
        return false;
    }

    default void onButtonClicked(Player player, int id) {
        if (id == getButtonId()) {
            onButtonClicked(player, id);
        }
    }

    default int getButtonId() {
        return -1;
    }

    default int[] getButtonIds() {
        int id = getButtonId();
        return id == -1 ? new int[0] : new int[]{id};
    }

}
