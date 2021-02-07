package de.vrsal.vanillaautomation.core.entity;

import de.vrsal.vanillaautomation.core.block.tileentity.TileXPHopper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public abstract class HopperCartEntityBase extends ContainerMinecartEntity implements IHopper {
    private final BlockPos lastPosition = BlockPos.ZERO;
    protected BlockState displayTile;
    private boolean isBlocked = true;
    private int transferTicker = -1;

    protected HopperCartEntityBase(EntityType<?> type, World worldIn, BlockState displayTile) {
        super(type, worldIn);
        this.displayTile = displayTile;
    }

    protected HopperCartEntityBase(EntityType<?> type, World worldIn, double x, double y, double z, BlockState displayTile) {
        super(type, x, y, z, worldIn);
        this.displayTile = displayTile;
    }

    @Override
    public Type getMinecartType() {
        return Type.CHEST;
    }

    @Override
    public BlockState getDisplayTile() {
        return getDefaultDisplayTile();
    }

    @Override
    public BlockState getDefaultDisplayTile() {
        return displayTile;
    }

    @Override
    public int getDefaultDisplayTileOffset() {
        return 1;
    }

    @Override
    public void killMinecart(DamageSource source) {
        super.killMinecart(source);
        if (!source.isExplosion() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.entityDropItem(this.getDisplayTile().getBlock());
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Nullable
    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public double getXPos() {
        return getPosX();
    }

    @Override
    public double getYPos() {
        return getPosY() + 0.5D;
    }

    @Override
    public double getZPos() {
        return getPosZ();
    }

    @Override
    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
        boolean flag = !receivingPower;
        if (flag != this.getBlocked()) {
            this.setBlocked(flag);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isRemote && this.isAlive() && this.getBlocked()) {
            BlockPos blockpos = this.getPosition();
            if (blockpos.equals(this.lastPosition)) {
                --this.transferTicker;
            } else {
                this.setTransferTicker(0);
            }

            if (!this.canTransfer()) {
                this.setTransferTicker(0);
                additionalHopperAction();
                if (this.captureDroppedItems()) {
                    this.setTransferTicker(4);
                    this.markDirty();
                }
            }
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("TransferCooldown", this.transferTicker);
        compound.putBoolean("Enabled", this.isBlocked);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.transferTicker = compound.getInt("TransferCooldown");
        this.isBlocked = !compound.contains("Enabled") || compound.getBoolean("Enabled");
    }

    public void setTransferTicker(int transferTickerIn) {
        this.transferTicker = transferTickerIn;
    }

    public boolean canTransfer() {
        return this.transferTicker > 0;
    }

    public boolean getBlocked() {
        return this.isBlocked;
    }

    public void setBlocked(boolean blocked) {
        this.isBlocked = blocked;
    }

    public boolean captureDroppedItems() {
        if (HopperTileEntity.pullItems(this)) {
            return true;
        } else {
            List<ItemEntity> list = this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(0.25D, 0.0D, 0.25D), EntityPredicates.IS_ALIVE);
            if (!list.isEmpty()) {
                HopperTileEntity.captureItem(this, list.get(0));
            }

            return false;
        }
    }

    protected abstract void additionalHopperAction();
}
