package com.mrpup.clumapi.component.tank;

import com.mrpup.clumapi.component.IGuiComponent;
import com.mrpup.clumapi.component.ISerializableComponent;
import com.mrpup.clumapi.menus.BaseComponentMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.List;

public class FluidTankComponent implements IGuiComponent, ISerializableComponent {

    private final String name;
    private final int xPos;
    private final int yPos;
    private final FluidTank tank;
    private final int buttonId;

    private int syncedAmount = 0;
    private int syncedFluidId = -1;

    public FluidTankComponent(String name, int xPos, int yPos, int capacity, int buttonId) {
        this.name = name;
        this.xPos = xPos;
        this.yPos = yPos;
        this.tank = new FluidTank(capacity);
        this.buttonId = buttonId;
    }

    public FluidTank getTank() {
        return tank;
    }

    public int getCapacity() {
        return tank.getCapacity();
    }

    public int getWidth() {
        if (getCapacity() == 10000) {
            return FluidTankTypes.TankType.TANK.getTexture().width();
        }
        if (getCapacity() == 4000) {
            return FluidTankTypes.TankType.TANK_SMALL.getTexture().width();
        }
        return 0;
    }

    public int getHeight() {
        if (getCapacity() == 10000) {
            return FluidTankTypes.TankType.TANK.getTexture().height();
        }
        if (getCapacity() == 4000) {
            return FluidTankTypes.TankType.TANK_SMALL.getTexture().height();
        }
        return 0;
    }

    @Override
    public void addSlots(BaseComponentMenu menu) {

    }

    @Override
    public void render(GuiGraphics graphics, int leftPos, int topPos) {
        int x = leftPos + xPos;
        int y = topPos + yPos;

        if (syncedAmount > 0 && syncedFluidId >= 0) {
            Fluid fluid = BuiltInRegistries.FLUID.byId(syncedFluidId);
            if (fluid != Fluids.EMPTY) {
                float ratio = (float) syncedAmount / getCapacity();
                int tintColor = IClientFluidTypeExtensions
                        .of(fluid).getTintColor();

                graphics.fill(x + 3, y + 3 + Math.round((getHeight() - 2) * (1 - ratio)),
                        x + getWidth() - 3, y + getHeight() - 3,
                        tintColor);
            }
        }

        if (getCapacity() == 10000) FluidTankTypes.TankType.TANK.render(graphics, x, y);
        if (getCapacity() == 4000) FluidTankTypes.TankType.TANK_SMALL.render(graphics, x, y);
    }

    @Override
    public List<DataSlot> getDataSlots() {
        return List.of(
                new DataSlot() {
                    @Override public int get() { return tank.getFluidAmount(); }
                    @Override public void set(int value) { syncedAmount = value; }
                },
                new DataSlot() {
                    @Override public int get() {
                        Fluid fluid = tank.getFluid().getFluid();
                        return BuiltInRegistries.FLUID.getId(fluid);
                    }
                    @Override public void set(int value) { syncedFluidId = value; }
                }
        );
    }

    @Override
    public List<Component> getTooltipLines() {
        FluidStack stack = syncedFluidId >= 0
                ? new FluidStack(BuiltInRegistries.FLUID.byId(syncedFluidId), syncedAmount)
                : FluidStack.EMPTY;

        if (stack.isEmpty()) {
            return List.of(Component.literal(ChatFormatting.GRAY + "Empty"));
        }

        return List.of(
                Component.translatable("tooltip.clumapi.fluid_info", Component.translatable(stack.getFluid().getFluidType().getDescriptionId()).withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.AQUA),
                Component.literal(ChatFormatting.YELLOW + "Amount: " + ChatFormatting.WHITE + syncedAmount + " / " + getCapacity() + " mB")
        );
    }

    @Override
    public boolean isHovered(int mouseX, int mouseY, int leftPos, int topPos) {
        int x = leftPos + xPos;
        int y = topPos + yPos;
        return mouseX >= x && mouseX < x + getWidth() && mouseY >= y && mouseY < y + getHeight();
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
    public String getSaveKey() {
        return name;
    }

    @Override
    public int getButtonId() {
        return buttonId;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int leftPos, int topPos) {
        if (button != 0) return false;
        if (!isHovered((int) mouseX, (int) mouseY, leftPos, topPos)) return false;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.gameMode == null) return false;

        mc.gameMode.handleInventoryButtonClick(mc.player.containerMenu.containerId, buttonId);
        return true;
    }

    @Override
    public void onButtonClicked(Player player, int id) {
        if (id != buttonId) return;

        ItemStack carried = player.containerMenu.getCarried();
        if (carried.isEmpty()) return;

        FluidActionResult fillTankResult = FluidUtil.tryEmptyContainer(carried, tank, FluidType.BUCKET_VOLUME, player, true);
        if (fillTankResult.isSuccess()) {
            applyCarriedResult(player, carried, fillTankResult.getResult());
            return;
        }

        FluidActionResult fillBucketResult = FluidUtil.tryFillContainer(carried, tank, FluidType.BUCKET_VOLUME, player, true);
        if (fillBucketResult.isSuccess()) {
            applyCarriedResult(player, carried, fillBucketResult.getResult());
        }
    }

    private void applyCarriedResult(Player player, ItemStack originalCarried, ItemStack resultStack) {
        if (player.getAbilities().instabuild) {
            return;
        }

        if (originalCarried.getCount() == 1) {
            player.containerMenu.setCarried(resultStack);
        } else {
            originalCarried.shrink(1);
            if (!player.getInventory().add(resultStack)) {
                player.drop(resultStack, false);
            }
        }
    }

    @Override
    public void saveComponent(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag tankTag = tank.writeToNBT(registries, new CompoundTag());
        tag.put(getSaveKey(), tankTag);
    }

    @Override
    public void loadComponent(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains(getSaveKey())) {
            tank.readFromNBT(registries, tag.getCompound(getSaveKey()));
        }
    }
}