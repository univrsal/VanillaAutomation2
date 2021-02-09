package de.vrsal.vanillaautomation.core.block.tileentity;

import de.vrsal.vanillaautomation.core.container.FilteredHopperContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;

import java.util.stream.IntStream;

public class TileFilteredHopper extends TileHopperBase {
    public static final int WHITELIST = 0;
    public static final int META = 1;
    public static final int NBT = 2;
    public static final int MOD = 3;
    private boolean whitelist = true;
    private boolean matchMeta = true;
    private boolean matchNBT = false;
    private boolean matchMod = false;

    public final IIntArray fields = new IIntArray() {

        @Override
        public int get(int index) {
            if (index == WHITELIST)
                return TileFilteredHopper.this.whitelist ? 1 : 0;
            else if (index == META)
                return TileFilteredHopper.this.matchMeta ? 1 : 0;
            else if (index == NBT)
                return TileFilteredHopper.this.matchNBT ? 1 : 0;
            else if (index == MOD)
                return TileFilteredHopper.this.matchMod ? 1 : 0;
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == WHITELIST)
                TileFilteredHopper.this.whitelist = value != 0;
            else if (index == META)
                TileFilteredHopper.this.matchMeta = value != 0;
            else if (index == NBT)
                TileFilteredHopper.this.matchNBT = value != 0;
            else if (index == MOD)
                TileFilteredHopper.this.matchMod = value != 0;
        }

        @Override
        public int size() {
            return 4;
        }
    };

    public void setMatchMeta(boolean b)
    {
        this.matchMeta = b;
    }

    public void setMatchNBT(boolean b)
    {
        this.matchNBT = b;
    }

    public void setMatchMod(boolean b)
    {
       this.matchMod = b;
    }

    public void setWhitelist(boolean b)
    {
        this.whitelist = b;
    }

    public TileFilteredHopper() {
        super(ModTiles.FILTERED_HOPPER.get(), 10, "filtered_hopper");
        this.pullFunction = this::pullItems;
    }

    private static IntStream getValidSlotsForSide(IInventory p_213972_0_, Direction p_213972_1_) {
        return p_213972_0_ instanceof ISidedInventory ? IntStream.of(((ISidedInventory) p_213972_0_).getSlotsForFace(p_213972_1_)) : IntStream.range(0, p_213972_0_.getSizeInventory());
    }

    private static boolean isInventoryEmpty(IInventory inventoryIn, Direction side) {
        return getValidSlotsForSide(inventoryIn, side).allMatch((index) -> inventoryIn.getStackInSlot(index).isEmpty());
    }

    public static String getModName(ItemStack s) {
        if (s.isEmpty())
            return "";
        String modID = "";
        if (s.getItem().getRegistryName() != null)
            modID = s.getItem().getRegistryName().getNamespace();
        return modID.isEmpty() ? "Minecraft" : modID;
    }

    private static boolean canExtractItemFromSlot(IInventory inventoryIn, ItemStack stack, int index, Direction side) {
        return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory) inventoryIn).canExtractItem(index, stack, side);
    }

    private boolean pullItemFromSlot(IHopper hopper, IInventory inventoryIn, int index, Direction direction) {
        ItemStack itemstack = inventoryIn.getStackInSlot(index);
        if (!itemstack.isEmpty() && canExtractItemFromSlot(inventoryIn, itemstack, index, direction) && isItemStackAllowed(itemstack)) {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = HopperTileEntity.putStackInInventoryAllSlots(inventoryIn, hopper, inventoryIn.decrStackSize(index, 1), (Direction) null);
            if (itemstack2.isEmpty()) {
                inventoryIn.markDirty();
                return true;
            }

            inventoryIn.setInventorySlotContents(index, itemstack1);
        }

        return false;
    }

    @Override
    public int getSizeInventoryForOutput() {
        return 5;
    }

    private boolean adjust(boolean in) {
        return whitelist == in;
    }

    public boolean isItemStackAllowed(ItemStack in) {
        boolean matchesItem = false, matchesMeta = false, matchesNBT = false, matchesMod = false;

        for (int i = getSizeInventoryForOutput(); i < getSizeInventory(); i++) {
            if (getStackInSlot(i).getItem().equals(in.getItem())) {
                matchesItem = true;
                if (getStackInSlot(i).getDamage() == in.getDamage())
                    matchesMeta = true;
                if (getStackInSlot(i).getOrCreateTag().equals(in.getOrCreateTag()))
                    matchesNBT = true;
            }
            if (getModName(getStackInSlot(i)).equals(getModName(in)))
                matchesMod = true;
        }

        if (whitelist) {
            return (matchesMod || matchesItem) && (!matchMeta || matchesMeta) && (!matchNBT || matchesNBT) &&
                    (!matchMod || matchesMod);
        }
        return (!matchesItem && !matchMod) || (matchMeta && !matchesMeta) || (matchNBT && !matchesNBT) || (matchMod && !matchesMod);
    }

    @Override
    public void tick() {
        super.tick();
    }

    public boolean pullItems(IHopper hopper) {
        Boolean ret = net.minecraftforge.items.VanillaInventoryCodeHooks.extractHook(hopper);
        if (ret != null) return ret;
        IInventory iinventory = HopperTileEntity.getSourceInventory(hopper);
        if (iinventory != null) {
            Direction direction = Direction.DOWN;
            System.out.println(isInventoryEmpty(iinventory, direction));
            return !isInventoryEmpty(iinventory, direction) && getValidSlotsForSide(iinventory, direction).anyMatch((index)
                    -> pullItemFromSlot(hopper, iinventory, index, direction));
        } else {
            for (ItemEntity itementity : HopperTileEntity.getCaptureItems(hopper)) {
                if (isItemStackAllowed(itementity.getItem()) && HopperTileEntity.captureItem(hopper, itementity)) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index >= getSizeInventoryForOutput()) {
            // Filter slots
            return false;
        } else {
            return isItemStackAllowed(stack);
        }
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return FilteredHopperContainer.create(id, player, this, fields);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putBoolean("MatchMeta", matchMeta);
        compound.putBoolean("MatchNBT", matchNBT);
        compound.putBoolean("MatchMod", matchMod);
        compound.putBoolean("Whitelist", matchMod);
        return super.write(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
    }
}
