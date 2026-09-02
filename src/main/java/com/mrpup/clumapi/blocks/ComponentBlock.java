package com.mrpup.clumapi.blocks;

import com.mrpup.clumapi.blocks.entity.ComponentBlockEntity;
import com.mrpup.clumapi.component.IGuiComponent;
import com.mrpup.clumapi.component.tank.FluidTankComponent;
import com.mrpup.clumapi.menus.ComponentMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class ComponentBlock<T extends ComponentBlockEntity> extends Block implements EntityBlock {

    private final Class<T> tileClass;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;


    public ComponentBlock(Properties properties, Class<T> tileClass) {
        super(properties);
        this.tileClass = tileClass;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public abstract BlockEntityType.BlockEntitySupplier<T> getTileFactory();

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return getTileFactory().create(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ComponentBlockEntity componentBlockEntity) {
            return componentBlockEntity.getLight();
        }
        return super.getLightEmission(state, level, pos);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof ComponentBlockEntity cbe)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        FluidTankComponent tankComponent = findFirstTank(cbe);
        if (tankComponent == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

       String tankComponentName = findFirstTankName(cbe);
        if (Objects.equals(tankComponentName, "output_tank")) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        IFluidHandler tank = tankComponent.getTank();

        FluidActionResult emptyResult = FluidUtil.tryEmptyContainer(stack, tank, FluidType.BUCKET_VOLUME, player, true);
        if (emptyResult.isSuccess()) {
            giveBackContainer(stack, emptyResult.getResult(), player, hand);
            cbe.setChanged();
            return ItemInteractionResult.CONSUME;
        }

        FluidActionResult fillResult = FluidUtil.tryFillContainer(stack, tank, FluidType.BUCKET_VOLUME, player, true);
        if (fillResult.isSuccess()) {
            giveBackContainer(stack, fillResult.getResult(), player, hand);
            cbe.setChanged();
            return ItemInteractionResult.CONSUME;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Nullable
    private FluidTankComponent findFirstTank(ComponentBlockEntity cbe) {
        for (IGuiComponent component : cbe.getComponents()) {
            if (component instanceof FluidTankComponent tank) {
                return tank;
            }
        }
        return null;
    }

    @Nullable
    private String findFirstTankName(ComponentBlockEntity cbe) {
        for (IGuiComponent component : cbe.getComponents()) {
            if (component instanceof FluidTankComponent tank) {
                return tank.getSaveKey();
            }
        }
        return null;
    }

    private void giveBackContainer(ItemStack original, ItemStack result, Player player, InteractionHand hand) {
        if (player.getAbilities().instabuild) {
            return;
        }

        if (original.getCount() == 1) {
            player.setItemInHand(hand, result);
        } else {
            original.shrink(1);
            if (!player.getInventory().add(result)) {
                player.drop(result, false);
            }
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof ComponentBlockEntity cbe)) return InteractionResult.PASS;

        if (!cbe.canPlayerAccess(player)) {
            player.displayClientMessage(
                    Component.literal("§cThis block locked by: " + cbe.getOwnerName()),
                    true
            );
            return InteractionResult.FAIL;
        }

        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable(getDescriptionId());
            }

            @Override
            public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
                return new ComponentMenu(cbe.getMenuType(), id, inv, cbe);
            }

            @Override
            public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buf) {
                buf.writeBlockPos(pos);
            }
        });

        return InteractionResult.CONSUME;
    }
}