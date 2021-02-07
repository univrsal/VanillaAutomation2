package de.vrsal.vanillaautomation.core.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SlotGhost extends Slot {

    public SlotGhost(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean canTakeStack(PlayerEntity playerIn) {
        putStack(ItemStack.EMPTY);
        return false;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        ItemStack s = stack.copy();
        s.setCount(1);
        putStack(s);
        return false;
    }
}
