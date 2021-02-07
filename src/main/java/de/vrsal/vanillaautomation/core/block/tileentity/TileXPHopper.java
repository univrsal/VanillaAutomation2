package de.vrsal.vanillaautomation.core.block.tileentity;

import de.vrsal.vanillaautomation.core.container.XPHopperContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class TileXPHopper extends TileHopperBase {

	public static final int xpPerBottle = 13;
	public static final int emptyBottleSlot = 5;
	private int progress = 0;

	public final IIntArray fields = new IIntArray() {

		@Override
		public int get(int index) {
			if (index == 0)
				return TileXPHopper.this.progress;
			return 0;
		}

		@Override
		public void set(int index, int value) {
			if (index == 0)
				TileXPHopper.this.progress = value;
		}

		@Override
		public int size() {
			return 1;
		}
	};

	public TileXPHopper() {
		super(ModTiles.XP_HOPPER.get(), 6, "xp_hopper");
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player) {
		return XPHopperContainer.createContainer(id, player, this, fields);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("Progress", progress);
		return super.write(compound);
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		progress = nbt.getInt("Progress");
		super.read(state, nbt);
	}

	public int getProgress() {
		return progress;
	}

	/* === Hopper stuff === */

	public static int captureXP(World worldIn, BlockPos where, IInventory to, int progress)
	{
		int newProgress = progress;
		List<ExperienceOrbEntity> orbs = worldIn.getEntitiesWithinAABB(ExperienceOrbEntity.class,
				new AxisAlignedBB(where).grow(1));

		if (!orbs.isEmpty()) {
			for (ExperienceOrbEntity orb : orbs) {
				int slot = getNextFreeBottleSlot(to);
				int resultXP = orb.xpValue + progress;
				ItemStack bottles = getBottles(to);

				if (!bottles.isEmpty()) {
					if (resultXP >= xpPerBottle && slot >= 0) {
						ItemStack output = to.getStackInSlot(slot);
						if (output.isEmpty())
							output = new ItemStack(Items.EXPERIENCE_BOTTLE, 1);
						else
							output.grow(1);

						bottles.shrink(1);
						to.setInventorySlotContents(emptyBottleSlot, bottles);
						to.setInventorySlotContents(slot, output);
						to.markDirty();
						newProgress = resultXP - xpPerBottle;

					} else {
						// We don't have space for a new bottle, but this orb would require a new one
						if (resultXP > xpPerBottle)
							break;

						newProgress = resultXP;
						orb.remove();
					}
				}
			}
		}
		return newProgress;
	}

	public static int getNextFreeBottleSlot(IInventory inv) {
		for (int i = 0; i < inv.getSizeInventory() - 1; i++) {
			ItemStack s = inv.getStackInSlot(i);
			if (s.isEmpty() || s.getItem().equals(Items.EXPERIENCE_BOTTLE) && s.getCount() < s.getMaxStackSize())
				return i;
		}
		return -1;
	}

	public static ItemStack getBottles(IInventory inv) {
		return inv.getStackInSlot(emptyBottleSlot);
	}

	@Override
	protected void customHopperAction() {
		progress = captureXP(world, getPos().up(), this, progress);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		// Only allow empty bottles into the bottle slot
		if (stack.getItem() == Items.GLASS_BOTTLE)
			return index == emptyBottleSlot;
		return true;
	}
}
