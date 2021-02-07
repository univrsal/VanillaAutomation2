package de.vrsal.vanillaautomation.core.block.tileentity;

import de.vrsal.vanillaautomation.VanillaAutomation;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.*;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.Supplier;
import java.util.stream.IntStream;

public abstract class TileHopperBase extends LockableLootTileEntity implements IHopper, ITickableTileEntity {
    protected NonNullList<ItemStack> hopperInventory;
    protected int transferCooldown = 0;
    protected String defaultName;

    public TileHopperBase(TileEntityType<? extends TileHopperBase> t, int invSize, String defaultName) {
        super(t);
        this.hopperInventory = NonNullList.<ItemStack>withSize(invSize, ItemStack.EMPTY);
        this.defaultName = VanillaAutomation.MOD_ID + ".container." + defaultName;
    }

    @Override
    public ITextComponent getDefaultName() {
        return new TranslationTextComponent(this.defaultName);
    }

    @Override
    public int getSizeInventory() {
        return this.hopperInventory.size();
    }

    @Override
    public double getXPos() {
        return (double) this.pos.getX() + 0.5D;
    }

    @Override
    public double getYPos() {
        return (double) this.pos.getY() + 0.5D;
    }

    @Override
    public double getZPos() {
        return (double) this.pos.getZ() + 0.5D;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return hopperInventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        hopperInventory = itemsIn;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if (!this.checkLootAndWrite(compound)) {
            ItemStackHelper.saveAllItems(compound, this.hopperInventory);
        }
        return super.write(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        if (!this.checkLootAndRead(nbt)) {
            ItemStackHelper.loadAllItems(nbt, this.hopperInventory);
        }
        super.read(state, nbt);
    }

    /* Hopper stuff */
    private IInventory getInventoryForHopperTransfer() {
        Direction direction = this.getBlockState().get(HopperBlock.FACING);
        return HopperTileEntity.getInventoryAtPosition(this.getWorld(), this.pos.offset(direction));
    }

    protected boolean isFull() {
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack itemstack = getStackInSlot(i);
            if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize())
                return false;
        }
        return true;
    }

    private static IntStream getSlotsForDirection(IInventory inventoryIn, Direction dir) {
        return inventoryIn instanceof ISidedInventory
                ? IntStream.of(((ISidedInventory) inventoryIn).getSlotsForFace(dir))
                : IntStream.range(0, inventoryIn.getSizeInventory());
    }

    protected boolean isInventoryFull(IInventory inventoryIn, Direction side) {
        return getSlotsForDirection(inventoryIn, side).allMatch((slot) -> {
            ItemStack itemstack = inventoryIn.getStackInSlot(slot);
            return itemstack.getCount() >= itemstack.getMaxStackSize();
        });
    }

    @Override
    public void tick() {
        if (!this.world.isRemote) {
            --this.transferCooldown;
            if (!this.isOnTransferCooldown()) {
                this.setTransferCooldown(0);
                this.updateHopper(() -> HopperTileEntity.pullItems(this));
            }
        }
    }

    protected boolean transferItemsOut() {
        IInventory iinventory = this.getInventoryForHopperTransfer();
        if (iinventory == null) {
            return false;
        } else {
            Direction direction = this.getBlockState().get(HopperBlock.FACING).getOpposite();
            if (this.isInventoryFull(iinventory, direction)) {
                return false;
            } else {
                for (int i = 0; i < this.getSizeInventory() - 1; ++i) { // -1 because we don't want to transfer out the
                    // bottles
                    if (!this.getStackInSlot(i).isEmpty()) {
                        ItemStack itemstack = this.getStackInSlot(i).copy();
                        ItemStack itemstack1 = HopperTileEntity.putStackInInventoryAllSlots(this, iinventory,
                                this.decrStackSize(i, 1), direction);
                        if (itemstack1.isEmpty()) {
                            iinventory.markDirty();
                            return true;
                        }

                        this.setInventorySlotContents(i, itemstack);
                    }
                }

                return false;
            }
        }
    }

    private boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }

    public void setTransferCooldown(int transferCooldown) {
        this.transferCooldown = transferCooldown;
    }

    protected abstract void customHopperAction();

    protected boolean updateHopper(Supplier<Boolean> sup) {
        if (this.world == null || this.world.isRemote || !this.getBlockState().get(HopperBlock.ENABLED))
            return false;

        customHopperAction();
        if (!this.isOnTransferCooldown()) {
            boolean flag = false;

            if (!this.isEmpty()) {
                flag = this.transferItemsOut();
            }

            if (!this.isFull()) {
                flag |= sup.get();
            }

            if (flag) {
                this.setTransferCooldown(8);
                this.markDirty();
                return true;
            }
        }
        return false;
    }
}
