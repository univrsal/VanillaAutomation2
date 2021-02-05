package de.vrsal.vanillaautomation.core.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class SlotFiltered extends Slot {

	private ItemStack filter;
	public SlotFiltered(IInventory inventoryIn, int index, int xPosition, int yPosition, ItemStack filter) {
		super(inventoryIn, index, xPosition, yPosition);
		this.filter = filter;
	}
	
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		return stack.isEmpty() || stack.getItem() != Items.AIR && stack.getItem().equals(filter.getItem());
	}

}
