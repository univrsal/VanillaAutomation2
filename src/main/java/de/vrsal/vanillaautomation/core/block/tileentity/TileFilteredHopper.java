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
                return TileFilteredHopper.this.matchNBT ? 1 : 0;
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
        fields.set(META, b ? 1 : 0);
    }

    public void setMatchNBT(boolean b)
    {
        fields.set(NBT, b ? 1 : 0);
    }

    public void setMatchMod(boolean b)
    {
        fields.set(MOD, b ? 1 : 0);
    }

    public void setWhitelist(boolean b)
    {
        fields.set(WHITELIST, b ? 1 : 0);
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

    private boolean adjust(boolean in) {
        return whitelist == in;
    }

    public boolean isItemStackAllowed(ItemStack in) {
        boolean matchesItem = false, matchesMeta = false, matchesNBT = false, matchesMod = false;

        for (int i = getSizeInventoryForOutput(); i < getSizeInventory(); i++) {
            if (getStackInSlot(i).getItem().equals(in.getItem())) {
                matchesItem = true;
                break;
            }
        }

        for (int i = getSizeInventoryForOutput(); i < getSizeInventory(); i++) {
            if (getStackInSlot(i).getDamage() == in.getDamage()) {
                matchesMeta = true;
                break;
            }
        }

        for (int i = getSizeInventoryForOutput(); i < getSizeInventory(); i++) {
            if (getModName(getStackInSlot(i)).equals(getModName(in))) {
                matchesMod = true;
                break;
            }
        }

        if (in.hasTag()) {
            for (int i = getSizeInventoryForOutput(); i < getSizeInventory(); i++) {
                if (getStackInSlot(i).getOrCreateTag().equals(in.getOrCreateTag())) {
                    matchesNBT = true;
                    break;
                }
            }
        }

        return adjust(matchesItem) && (!matchMeta || adjust(matchesMeta)) && (!matchNBT || adjust(matchesNBT)) &&
                (!matchMod || adjust(matchesMod));
    }

    public boolean pullItems(IHopper hopper) {
        Boolean ret = net.minecraftforge.items.VanillaInventoryCodeHooks.extractHook(hopper);
        if (ret != null) return ret;
        IInventory iinventory = HopperTileEntity.getSourceInventory(hopper);
        if (iinventory != null) {
            Direction direction = Direction.DOWN;
            return !isInventoryEmpty(iinventory, direction) && getValidSlotsForSide(iinventory, direction).anyMatch((index)
                    -> pullItemFromSlot(hopper, iinventory, index, direction));
        } else {
            for (ItemEntity itementity : HopperTileEntity.getCaptureItems(hopper)) {
                if (HopperTileEntity.captureItem(hopper, itementity)) {
                    return true;
                }
            }

            return false;
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
