package com.mrpup.clumapi.blocks;

import com.mrpup.clumapi.blocks.entity.ComponentBlockEntity;
import com.mrpup.clumapi.component.lock.ILockable;
import com.mrpup.clumapi.component.lock.LockComponent;
import com.mrpup.clumapi.component.progress.ProgressComponent;
import com.mrpup.clumapi.component.progress.ProgressTypes;
import com.mrpup.clumapi.component.redstone.IRedstoneControllable;
import com.mrpup.clumapi.component.redstone.RedstoneComponent;
import com.mrpup.clumapi.component.storage.EnergyShowerComponent;
import com.mrpup.clumapi.component.storage.EnergyStorageComponent;
import com.mrpup.clumapi.component.storage.EnergyStorageProvider;
import com.mrpup.clumapi.component.storage.IEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.UUID;

public abstract class BaseMachineTile extends ComponentBlockEntity implements EnergyStorageProvider, ILockable, IRedstoneControllable, IEnergy {

    private final EnergyStorageComponent energyStorage;
    private final EnergyShowerComponent energyShowerComponent;
    private final ProgressComponent progressBar;
    private final RedstoneComponent redstoneModeComponent;
    private final LockComponent lockModeComponent;

    private String ownerName = "";
    private UUID ownerUUID = null;

    private int capacity = 10000;
    private int perTickEnergy = 40;
    private int receive = 20000000;

    private int redstoneMode = 0;
    private int lockMode = 0;
    private int lockMethod = 0;

    private float progressAccumulator = 0f;

    public static final int ENERGY_X = 6, ENERGY_Y = 20;
    public static int PROGRESS_X = 6;
    public static int PROGRESS_Y = 20;
    public static ProgressTypes.ProgressType ARROW_TYPE;

    public BaseMachineTile(BlockEntityType<?> type, BlockPos pos, BlockState state, int xProg, int yProg, int progressTicks, ProgressTypes.ProgressType arrow) {
        super(type, pos, state);

        energyStorage = new EnergyStorageComponent(capacity, receive, 0, ENERGY_X, ENERGY_Y);
        addComponent(energyStorage);

        energyShowerComponent = new EnergyShowerComponent(6, 78, this);
        addComponent(energyShowerComponent);

        progressBar = new ProgressComponent(xProg, yProg, progressTicks, arrow);
        addComponent(progressBar);

        PROGRESS_X = xProg;
        PROGRESS_Y = yProg;
        ARROW_TYPE = arrow;

        redstoneModeComponent = new RedstoneComponent(150, 78, this, 0);
        addComponent(redstoneModeComponent);

        lockModeComponent = new LockComponent(-27, 4, this, 1, 2);
        addComponent(lockModeComponent);
    }

    public float getSpeedMultiplier() {
        return 1f;
    }

    public float getEfficiencyMultiplier() {
        return 1f;
    }

    public float getRadiusBonus() {
        return 1f;
    }

    public void energyComponent(int cap, int perTick) {
        this.capacity = cap;
        this.perTickEnergy = perTick;
        this.energyStorage.setCapacity(cap);
    }

    public static int getProgressX() {
        return PROGRESS_X;
    }

    public static int getProgressY() {
        return PROGRESS_Y;
    }

    public static ProgressTypes.ProgressType getArrowType() {
        return ARROW_TYPE;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getPerTickEnergy() {
        return perTickEnergy;
    }

    public boolean canRecipeBySettings() {
        return canRecipeByEnergy() && canRecipeByRedstone();
    }

    public boolean canRecipeByEnergy() {
        return energyStorage.getAmountAsInt() >= getPerTickEnergy();
    }

    public boolean canRecipeByRedstone() {
        if (this.level == null || this.level.isClientSide()) return true;

        boolean hasSignal = this.level.hasNeighborSignal(this.worldPosition);
        return switch (redstoneMode) {
            case 1 -> hasSignal;
            case 2 -> !hasSignal;
            default -> true;
        };
    }

    public EnergyStorageComponent getEnergyStorageMachine() {
        return energyStorage;
    }

    public int getEnergy() {
        return energyStorage.getAmountAsInt();
    }

    public void extractEnergy(int toExtract) {
        energyStorage.setEnergyStored(energyStorage.getAmountAsInt() - toExtract);
    }

    public ProgressComponent getProgressBar() {
        return progressBar;
    }

    public void setProgressTile(int prog) {
        progressBar.setProgress(prog);
        if (prog == 0) isWork = false;
    }

    public int getProgressTile() {
        return progressBar.getProgress();
    }

    public int getMaxProgressTile() {
        return progressBar.getMaxProgress();
    }

    public int getEffectiveEnergyCost(int baseCost) {
        return Math.max(1, Math.round(baseCost / getEfficiencyMultiplier()));
    }

    public boolean advanceProgress(int baseEnergyCost) {
        int energyCost = getEffectiveEnergyCost(baseEnergyCost) + (10 * (int) getSpeedMultiplier());

        if (energyStorage.getAmountAsInt() < energyCost) {
            return false;
        }

        extractEnergy(energyCost);

        progressAccumulator += getSpeedMultiplier();
        int wholeProgress = (int) progressAccumulator;
        progressAccumulator -= wholeProgress;

        setProgressTile(getProgressTile() + wholeProgress);

        if (this.level != null) {
            this.level.getLightEngine().checkBlock(this.worldPosition);
        }

        if (getProgressTile() >= getMaxProgressTile()) {
            setProgressTile(0);
            progressAccumulator = 0f;
            isWork = false;
            if (this.level != null) {
                this.level.getLightEngine().checkBlock(this.worldPosition);
            }
            return true;
        }
        return false;
    }


    public int getRedstoneMode() {
        return redstoneMode;
    }

    public void cycleRedstoneMode() {
        this.redstoneMode = (this.redstoneMode + 1) % 3;
        setChanged();
    }

    public int getLockMode() {
        return lockMode;
    }

    public void cycleLockMode(Player player) {
        this.lockMode = (this.lockMode + 1) % 2;
        if (this.lockMode == 1 && player != null) {
            this.ownerName = player.getGameProfile().name();
            this.ownerUUID = player.getUUID();
        }
        setChanged();
    }

    public int getLockMethod() {
        return lockMethod;
    }

    public void cycleLockMethod() {
        this.lockMethod = (this.lockMethod + 1) % 2;
        setChanged();
    }

    public boolean canPlayerAccess(Player player) {
        if (lockMode == 0) return true;
        if (player == null) return false;

        if (lockMethod == 0) {
            return ownerName != null && ownerName.equalsIgnoreCase(player.getGameProfile().name());
        }
        return ownerUUID != null && ownerUUID.equals(player.getUUID());
    }

    public String getOwnerName() {
        return ownerName;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public EnergyStorageComponent getEnergyStorage() {
        return getEnergyStorageMachine();
    }


    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("energy", getEnergy());
        output.putInt("progress", getProgressTile());
        output.putInt("redstoneMode", getRedstoneMode());
        output.putInt("lockMode", getLockMode());
        output.putInt("lockMethod", getLockMethod());
        output.putString("ownerName", ownerName == null ? "" : ownerName);
        output.storeNullable("ownerUUID", UUIDUtil.CODEC, ownerUUID);
        output.putFloat("progressAccumulator", progressAccumulator);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyStorage.setEnergyStored(input.getIntOr("energy", 0));
        progressBar.setProgress(input.getIntOr("progress", 0));
        redstoneMode = input.getIntOr("redstoneMode", 0);
        lockMode = input.getIntOr("lockMode", 0);
        lockMethod = input.getIntOr("lockMethod", 0);
        ownerName = input.getStringOr("ownerName", "");
        ownerUUID = input.read("ownerUUID", UUIDUtil.CODEC).orElse(null);
        progressAccumulator = input.getFloatOr("progressAccumulator", 0.0F);
    }
}
