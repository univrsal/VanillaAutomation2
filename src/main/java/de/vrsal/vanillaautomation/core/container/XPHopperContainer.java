package de.vrsal.vanillaautomation.core.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;

public class XPHopperContainer extends BaseContainer {
	private final IIntArray progress;
	
	protected XPHopperContainer(ContainerType<? extends BaseContainer> type, int id, PlayerInventory playerInventory) {
		this(type, id, playerInventory, new Inventory(6), new IntArray(1));
	}

	public int getProgress()
	{
		return progress.get(0);
	}

	public XPHopperContainer(ContainerType<? extends BaseContainer> type, int id, PlayerInventory playerInventory, IInventory inventory, IIntArray progress) {
		super(type, id, playerInventory, inventory);
		assertIntArraySize(progress, 1);
		assertInventorySize(inventory, 6);

		inventory.openInventory(playerInventory.player);
		
		for (int j = 0; j < 5; ++j) {
			this.addSlot(new Slot(inventory, j, 26 + j * 18, 20));
		}

		this.addSlot(new SlotFiltered(inventory, inventory.getSizeInventory() - 1, 134, 20,
				new ItemStack(Items.GLASS_BOTTLE, 1)));

		for (int l = 0; l < 3; ++l) {
			for (int k = 0; k < 9; ++k) {
				this.addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, l * 18 + 51));
			}
		}

		for (int i1 = 0; i1 < 9; ++i1) {
			this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 109));
		}

		this.progress = progress;
		this.trackIntArray(this.progress);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
	      ItemStack itemstack = ItemStack.EMPTY;
	      Slot slot = this.inventorySlots.get(index);
	      if (slot != null && slot.getHasStack()) {
	         ItemStack itemstack1 = slot.getStack();
	         itemstack = itemstack1.copy();
	         
	         if (itemstack1.getItem() == Items.GLASS_BOTTLE && index >= tileInventory.getSizeInventory()) {
	        	 if (!this.mergeItemStack(itemstack1, tileInventory.getSizeInventory() - 1, tileInventory.getSizeInventory(), false))
	        		 return ItemStack.EMPTY;
	         }
	         
	         if (index < this.tileInventory.getSizeInventory()) {
	            if (!this.mergeItemStack(itemstack1, this.tileInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
	               return ItemStack.EMPTY;
	            }
	         } else if (!this.mergeItemStack(itemstack1, 0, this.tileInventory.getSizeInventory(), false)) {
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

	public static XPHopperContainer create(int windowId, PlayerInventory playerInventory) {
		return new XPHopperContainer(ModContainers.XP_HOPPER.get(), windowId, playerInventory);
	}

	public static XPHopperContainer create(int id, PlayerInventory player, IInventory hopperInv, IIntArray fields) {
		return new XPHopperContainer(ModContainers.XP_HOPPER.get(), id, player, hopperInv, fields);
	}
}