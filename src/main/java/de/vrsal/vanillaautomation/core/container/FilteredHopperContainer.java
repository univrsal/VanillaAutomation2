package de.vrsal.vanillaautomation.core.container;

import de.vrsal.vanillaautomation.core.block.tileentity.IFilteredHopper;
import de.vrsal.vanillaautomation.core.block.tileentity.TileFilteredHopper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;

public class FilteredHopperContainer extends BaseContainer {
    private final IIntArray fields;

    IInventory hopperInventory = null;

    public FilteredHopperContainer(ContainerType<? extends BaseContainer> type, int id, PlayerInventory playerInventory, IInventory tileInventory, IIntArray fields)
    {
        super(type, id, playerInventory, tileInventory);
        assertIntArraySize(fields, 4);
        assertInventorySize(tileInventory, 5);

        tileInventory.openInventory(playerInventory.player);

        this.hopperInventory = tileInventory;
        int halfInv = tileInventory.getSizeInventory() / 2;

        // Hopper inv
        for (int j = 0; j < halfInv; ++j)
            this.addSlot(new Slot(tileInventory, j, 45 + j * 18, 20));

        // The Filters
        for (int j = halfInv; j < tileInventory.getSizeInventory(); ++j)
            this.addSlot(new SlotGhost(tileInventory, j, 45 + (j - halfInv) * 18, 40));

        // Player inv
        int i = 71;
        for (int l = 0; l < 3; ++l)
            for (int k = 0; k < 9; ++k)
                this.addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, l * 18 + i));


        for (int i1 = 0; i1 < 9; ++i1)
            this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 58 + i));

        this.fields = fields;
        this.trackIntArray(this.fields);
    }

    public IIntArray getFields() {
        return fields;
    }


    public FilteredHopperContainer(ContainerType<FilteredHopperContainer> type, int windowId, PlayerInventory playerInventory) {
        this(type, windowId, playerInventory, new Inventory(10), new IntArray(4));
    }

    public static FilteredHopperContainer create(int windowId, PlayerInventory playerInventory) {
        return new FilteredHopperContainer(ModContainers.FILTERED_HOPPER.get(), windowId, playerInventory);
    }

    public static FilteredHopperContainer create(int id, PlayerInventory player, IInventory hopperInv, IIntArray fields) {
        return new FilteredHopperContainer(ModContainers.FILTERED_HOPPER.get(), id, player, hopperInv, fields);
    }

    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < this.hopperInventory.getSizeInventory()) {
                if (!this.mergeItemStack(itemstack1, this.hopperInventory.getSizeInventory() / 2, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, this.hopperInventory.getSizeInventory() / 2, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}
