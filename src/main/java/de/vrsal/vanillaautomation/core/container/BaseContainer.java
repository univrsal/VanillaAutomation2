package de.vrsal.vanillaautomation.core.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

public class BaseContainer extends Container {
    protected final IInventory playerInventory;
    protected final IInventory tileInventory;

    public BaseContainer(ContainerType<? extends BaseContainer> type, int id, PlayerInventory playerInventory, IInventory tileInventory) {
        super(type, id);
        this.playerInventory = playerInventory;
        this.tileInventory = tileInventory;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return this.tileInventory.isUsableByPlayer(playerIn);
    }
}
