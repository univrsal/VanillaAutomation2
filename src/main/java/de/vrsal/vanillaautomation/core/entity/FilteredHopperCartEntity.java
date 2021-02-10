package de.vrsal.vanillaautomation.core.entity;

import de.vrsal.vanillaautomation.core.block.ModBlocks;
import de.vrsal.vanillaautomation.core.block.tileentity.IFilteredHopper;
import de.vrsal.vanillaautomation.core.block.tileentity.TileFilteredHopper;
import de.vrsal.vanillaautomation.core.container.FilteredHopperContainer;
import de.vrsal.vanillaautomation.core.container.ModContainers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.IIntArray;
import net.minecraft.world.World;

import java.util.List;

public class FilteredHopperCartEntity extends HopperCartEntityBase implements IFilteredHopper {

    private boolean whitelist = true;
    private boolean matchMeta = true;
    private boolean matchNBT = false;
    private boolean matchMod = false;

    public final IIntArray fields = new IIntArray() {

        @Override
        public int get(int index) {
            if (index == TileFilteredHopper.WHITELIST)
                return FilteredHopperCartEntity.this.whitelist ? 1 : 0;
            else if (index == TileFilteredHopper.META)
                return FilteredHopperCartEntity.this.matchMeta ? 1 : 0;
            else if (index == TileFilteredHopper.NBT)
                return FilteredHopperCartEntity.this.matchNBT ? 1 : 0;
            else if (index == TileFilteredHopper.MOD)
                return FilteredHopperCartEntity.this.matchMod ? 1 : 0;
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == TileFilteredHopper.WHITELIST)
                FilteredHopperCartEntity.this.whitelist = value != 0;
            else if (index == TileFilteredHopper.META)
                FilteredHopperCartEntity.this.matchMeta = value != 0;
            else if (index == TileFilteredHopper.NBT)
                FilteredHopperCartEntity.this.matchNBT = value != 0;
            else if (index == TileFilteredHopper.MOD)
                FilteredHopperCartEntity.this.matchMod = value != 0;
        }

        @Override
        public int size() {
            return 4;
        }
    };

    protected FilteredHopperCartEntity(EntityType<?> type, World worldIn) {
        super(type, worldIn, ModBlocks.FILTERED_HOPPER.get().getDefaultState());
    }

    public FilteredHopperCartEntity(World worldIn, double x, double y, double z) {
        super(ModEntities.FILTERED_HOPPER_CART.get(), worldIn, x, y, z, ModBlocks.FILTERED_HOPPER.get().getDefaultState());
    }

    @Override
    public void killMinecart(DamageSource source) {
        super.killMinecart(source);
    }

    @Override
    public void remove() {
        // Clear filters so they won't be dropped
        for (int i = getFirstFilterSlot(); i < getSizeInventory(); i++)
            setInventorySlotContents(i, ItemStack.EMPTY);
        super.remove();
    }

    @Override
    public void setDamage(float damage) {
        super.setDamage(damage);
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        matchMeta = nbt.getBoolean("MatchMeta");
        matchNBT = nbt.getBoolean("MatchNBT");
        matchMod = nbt.getBoolean("MatchMod");
        whitelist = nbt.getBoolean("Whitelist");
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putBoolean("MatchMeta", matchMeta);
        compound.putBoolean("MatchNBT", matchNBT);
        compound.putBoolean("MatchMod", matchMod);
        compound.putBoolean("Whitelist", whitelist);
        super.writeAdditional(compound);
    }

    @Override
    public boolean captureDroppedItems() {
        if (TileFilteredHopper.pullItems(this)) {
            return true;
        } else {
            List<ItemEntity> list = this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(0.25D, 0.0D, 0.25D), EntityPredicates.IS_ALIVE);
            if (!list.isEmpty() && TileFilteredHopper.isItemStackAllowed(list.get(0).getItem(), this)) {
                HopperTileEntity.captureItem(this, list.get(0));
            }
            return false;
        }
    }

    @Override
    protected void additionalHopperAction() {
        // NO-OP
    }

    @Override
    protected Container createContainer(int id, PlayerInventory playerInventoryIn) {
        return new FilteredHopperContainer(ModContainers.FILTERED_HOPPER.get(), id, playerInventoryIn, this, this.fields);
    }

    @Override
    public int getSizeInventory() {
        return 10;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index >= 5) {
            // Filter slots
            return false;
        } else {
            return TileFilteredHopper.isItemStackAllowed(stack, this);
        }
    }

    @Override
    public boolean whitelist() {
        return whitelist;
    }

    @Override
    public boolean matchMeta() {
        return matchMeta;
    }

    @Override
    public boolean matchMod() {
        return matchMod;
    }

    @Override
    public boolean matchNBT() {
        return matchNBT;
    }

    @Override
    public void setWhitelist(boolean b) {
        whitelist = b;
    }

    @Override
    public void setMatchMeta(boolean b) {
        matchMeta = b;
    }

    @Override
    public void setMatchMod(boolean b) {
        matchMod = b;
    }

    @Override
    public void setMatchNBT(boolean b) {
        matchNBT = b;
    }

    @Override
    public int getFirstFilterSlot() {
        return 5;
    }
}
