package com.mrpup.clumapi.component.tank;

import com.mrpup.clumapi.component.IGuiComponent;
import com.mrpup.clumapi.component.ISerializableComponent;
import com.mrpup.clumapi.fluids.FluidsHolder;
import com.mrpup.clumapi.fluids.RegFluids;
import com.mrpup.clumapi.menus.BaseComponentMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;

public class FluidTankComponent implements IGuiComponent, ISerializableComponent {

    private final String name;
    private final int xPos;
    private final int yPos;
    private final FluidStacksResourceHandler tank;
    private final int buttonId;

    private int syncedAmount = 0;
    private int syncedFluidId = -1;

    private final boolean outputOnly;
    private final boolean inputOnly;

    private final ResourceHandler<FluidResource> externalHandler;

    public FluidTankComponent(String name, int xPos, int yPos, int capacity, int buttonId) {
        this(name, xPos, yPos, capacity, buttonId, false, true);
    }

    public FluidTankComponent(String name, int xPos, int yPos, int capacity, int buttonId, boolean outputOnly, boolean inputOnly) {
        this.name = name;
        this.xPos = xPos;
        this.yPos = yPos;
        this.tank = new FluidStacksResourceHandler(1, capacity);
        this.buttonId = buttonId;
        this.outputOnly = outputOnly;
        this.inputOnly = inputOnly;

        if (outputOnly) {
            this.externalHandler = new OutputOnlyFluidHandler(tank);
        } else if (inputOnly) {
            this.externalHandler = new InputOnlyFluidHandler(tank);
        } else {
            this.externalHandler = tank;
        }
    }

    public FluidStacksResourceHandler getTank() {
        return tank;
    }

    public ResourceHandler<FluidResource> getHandler() {
        return externalHandler;
    }

    public int getCapacity() {
        return tank.getCapacityAsInt(0, FluidResource.EMPTY);
    }

    public FluidStack getFluid() {
        return FluidUtil.getStack(tank, 0);
    }

    public int getFluidAmount() {
        return tank.getAmountAsInt(0);
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
    public void render(GuiGraphicsExtractor graphics, int leftPos, int topPos) {
        int x = leftPos + xPos;
        int y = topPos + yPos;

        if (syncedAmount > 0 && syncedFluidId >= 0) {
            Fluid fluid = BuiltInRegistries.FLUID.byId(syncedFluidId);
            if (fluid != Fluids.EMPTY) {
                float ratio = Math.min(1f, (float) syncedAmount / getCapacity());

                FluidsHolder holder = RegFluids.getHolder(fluid);
                int tintColor = holder != null ? holder.getTintColor() : 0xFFFFFFFF;
                if (fluid == Fluids.WATER) tintColor = 0xFF3F76E4;

                int innerTop = y + 3;
                int innerBottom = y + getHeight() - 3;
                int innerHeight = innerBottom - innerTop;

                int fillHeight = Math.max(1, Math.round(innerHeight * ratio));

                graphics.fill(x + 3, innerBottom - fillHeight,
                        x + getWidth() - 3, innerBottom, tintColor);
            }
        }

        if (getCapacity() == 10000) FluidTankTypes.TankType.TANK.render(graphics, x, y);
        if (getCapacity() == 4000) FluidTankTypes.TankType.TANK_SMALL.render(graphics, x, y);
    }

    @Override
    public List<DataSlot> getDataSlots() {
        return List.of(
                new DataSlot() {
                    @Override public int get() { return getFluidAmount(); }
                    @Override public void set(int value) { syncedAmount = value; }
                },
                new DataSlot() {
                    @Override public int get() {
                        Fluid fluid = getFluid().getFluid();
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

        var access = ItemAccess.forPlayerCursor(player, player.containerMenu);
        var itemHandler = access.getCapability(Capabilities.Fluid.ITEM);
        if (itemHandler == null) return;

        if (!outputOnly && tryMove(itemHandler, tank)) return;
        tryMove(tank, itemHandler);
    }

    private boolean tryMove(ResourceHandler<FluidResource> from, ResourceHandler<FluidResource> to) {
        try (var tx = Transaction.openRoot()) {
            int moved = ResourceHandlerUtil.move(from, to, resource -> true, FluidType.BUCKET_VOLUME, tx);

            if (moved == FluidType.BUCKET_VOLUME) {
                tx.commit();
                return true;
            }
        }
        return false;
    }

    @Override public void saveComponent(ValueOutput output) {
        output.store(getSaveKey(), FluidStack.OPTIONAL_CODEC, FluidUtil.getStack(tank, 0));
    }

    @Override
    public void loadComponent(ValueInput input) {
        FluidStack stack = input .read(getSaveKey(), FluidStack.OPTIONAL_CODEC).orElse(FluidStack.EMPTY);
        if (stack.isEmpty()) {
            return;
        }

        try (var tx = Transaction.openRoot()) {
            int inserted = tank.insert(0, FluidResource.of(stack), stack.getAmount(), tx);
            if (inserted == stack.getAmount()) {
                tx.commit();
            }
        }
    }
}