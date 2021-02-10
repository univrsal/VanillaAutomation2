package de.vrsal.vanillaautomation.core.entity;

import de.vrsal.vanillaautomation.core.block.ModBlocks;
import de.vrsal.vanillaautomation.core.block.tileentity.TileXPHopper;
import de.vrsal.vanillaautomation.core.container.ModContainers;
import de.vrsal.vanillaautomation.core.container.XPHopperContainer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraft.world.World;

public class XPHopperCartEntity extends HopperCartEntityBase {
    int progress = 0;
    public final IIntArray fields = new IIntArray() {

        @Override
        public int get(int index) {
            if (index == 0)
                return XPHopperCartEntity.this.progress;
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0)
                XPHopperCartEntity.this.progress = value;
        }

        @Override
        public int size() {
            return 1;
        }
    };

    public XPHopperCartEntity(EntityType<? extends XPHopperCartEntity> type, World worldIn) {
        super(type, worldIn, ModBlocks.XP_HOPPER.get().getDefaultState());
    }

    public XPHopperCartEntity(World worldIn, double x, double y, double z) {
        super(ModEntities.XP_HOPPER_CART.get(), worldIn, x, y, z, ModBlocks.XP_HOPPER.get().getDefaultState());
    }

    @Override
    protected Container createContainer(int id, PlayerInventory playerInventoryIn) {
        return new XPHopperContainer(ModContainers.XP_HOPPER.get(), id, playerInventoryIn, this, this.fields);
    }

    @Override
    public int getSizeInventory() {
        return 6;
    }


    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Progress", this.progress);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.progress = compound.getInt("Progress");
    }

    @Override
    protected void additionalHopperAction() {
        progress = TileXPHopper.captureXP(world, getPosition().up(), this, progress);
    }
}