package com.mrpup.clumapi.blocks.entity;

import com.mrpup.clumapi.component.ComponentItemHandler;
import com.mrpup.clumapi.component.IGuiComponent;
import com.mrpup.clumapi.component.ISerializableComponent;
import com.mrpup.clumapi.component.InventoryComponent;
import com.mrpup.clumapi.component.bg.BGTypes;
import com.mrpup.clumapi.menus.ComponentMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ComponentBlockEntity extends BlockEntity {
    private final List<IGuiComponent> components = new ArrayList<>();
    private Supplier<MenuType<ComponentMenu>> menuTypeSupplier;
    private BGTypes.BGType bgType = BGTypes.BGType.BG;
    private IItemHandler itemHandler;

    protected boolean isWork = false;

    //private IBlockRenderer<? extends ComponentBlockEntity> renderer = null;

    public ComponentBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void setMenuType(Supplier<MenuType<ComponentMenu>> supplier) {
        this.menuTypeSupplier = supplier;
    }


    public MenuType<ComponentMenu> getMenuType() {
        return menuTypeSupplier != null ? menuTypeSupplier.get() : null;
    }

    public void setBgType(BGTypes.BGType bgType) {
        this.bgType = bgType;
    }

    public BGTypes.BGType getBgType() {
        if (bgType != null) {
            return bgType;
        }

        return BGTypes.BGType.BG;
    }

    public int getLight() {
        return this.isWork ? 15 : 0;
    }

    public boolean canPlayerAccess(Player player) {
        return true;
    }

    public String getOwnerName() {
        return null;
    }

    public void addComponent(IGuiComponent component) {
        components.add(component);
    }

    public List<IGuiComponent> getComponents() {
        return components;
    }

    public IItemHandler getItemHandler() {
        if (itemHandler == null) {
            List<IItemHandlerModifiable> handlers = new ArrayList<>();
            for (IGuiComponent component : components) {
                if (component instanceof InventoryComponent inv) {
                    handlers.add(new ComponentItemHandler(inv));
                }
            }
            itemHandler = handlers.isEmpty()
                    ? new ComponentItemHandler(new InventoryComponent("empty", 0, 0, 0, 0))
                    : new CombinedInvWrapper(handlers.toArray(new IItemHandlerModifiable[0]));
        }
        return itemHandler;
    }

    /*

    @SuppressWarnings("unchecked")
    public <T extends ComponentBlockEntity> void setRenderer(IBlockRenderer<T> renderer) {
        this.renderer = (IBlockRenderer<? extends ComponentBlockEntity>) renderer;
    }

    @SuppressWarnings("unchecked")
    public <T extends ComponentBlockEntity> IBlockRenderer<T> getRenderer() {
        return (IBlockRenderer<T>) renderer;
    }

    public boolean hasRenderer() {
        return renderer != null;
    }

     */

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        for (IGuiComponent component : components) {
            if (component instanceof ISerializableComponent serializable) {
                serializable.saveComponent(tag, registries);
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        for (IGuiComponent component : components) {
            if (component instanceof ISerializableComponent serializable) {
                serializable.loadComponent(tag, registries);
            }
        }
    }
}