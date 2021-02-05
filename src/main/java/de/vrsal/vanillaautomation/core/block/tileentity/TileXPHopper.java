package de.vrsal.vanillaautomation.core.block.tileentity;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import de.vrsal.vanillaautomation.VanillaAutomation;
import de.vrsal.vanillaautomation.core.container.XPHopperContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.entity.item.ExperienceOrbEntity;

public class TileXPHopper extends LockableLootTileEntity implements IHopper, ITickableTileEntity {
	private NonNullList<ItemStack> hopperInventory;
	public static final int xpPerBottle = 13;
	public static final int emptyBottleSlot = 5;
	private int transferCooldown = 0;
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
		super(ModTiles.XP_HOPPER.get());
		this.hopperInventory = NonNullList.<ItemStack>withSize(6, ItemStack.EMPTY);
	}

	@Override
	public int getSizeInventory() {
		return this.hopperInventory.size();
	}

	@Override
	public double getXPos() {
		return (double) this.pos.getX() + 0.5D;
	}

	@Override
	public double getYPos() {
		return (double) this.pos.getY() + 0.5D;
	}

	@Override
	public double getZPos() {
		return (double) this.pos.getZ() + 0.5D;
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return hopperInventory;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> itemsIn) {
		hopperInventory = itemsIn;
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent(VanillaAutomation.MOD_ID + ".container.xp_hopper");
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player) {
		return XPHopperContainer.createContainer(id, player, this);
	}

	@Override
	public void tick() {
		if (!this.world.isRemote) {
			--this.transferCooldown;
			if (!this.isOnTransferCooldown()) {
				this.setTransferCooldown(0);
				this.updateHopper(() -> {
					return HopperTileEntity.pullItems(this);
				});
			}
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("progress", progress);
		if (!this.checkLootAndWrite(compound)) {
			ItemStackHelper.saveAllItems(compound, this.hopperInventory);
		}
		return super.write(compound);
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		progress = nbt.getInt("progress");
		if (!this.checkLootAndRead(nbt)) {
			ItemStackHelper.loadAllItems(nbt, this.hopperInventory);
		}
		super.read(state, nbt);
	}

	public int getProgress() {
		return progress;
	}

	/* === Hopper stuff === */
	private IInventory getInventoryForHopperTransfer() {
		Direction direction = this.getBlockState().get(HopperBlock.FACING);
		return HopperTileEntity.getInventoryAtPosition(this.getWorld(), this.pos.offset(direction));
	}

	protected boolean isFull() {
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack itemstack = getStackInSlot(i);
			if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize())
				return false;
		}
		return true;
	}

	private static IntStream getSlotsForDirection(IInventory inventoryIn, Direction dir) {
		return inventoryIn instanceof ISidedInventory
				? IntStream.of(((ISidedInventory) inventoryIn).getSlotsForFace(dir))
				: IntStream.range(0, inventoryIn.getSizeInventory());
	}

	protected boolean isInventoryFull(IInventory inventoryIn, Direction side) {
		return getSlotsForDirection(inventoryIn, side).allMatch((slot) -> {
			ItemStack itemstack = inventoryIn.getStackInSlot(slot);
			return itemstack.getCount() >= itemstack.getMaxStackSize();
		});
	}

	protected boolean transferItemsOut() {
		IInventory iinventory = this.getInventoryForHopperTransfer();
		if (iinventory == null) {
			return false;
		} else {
			Direction direction = this.getBlockState().get(HopperBlock.FACING).getOpposite();
			if (this.isInventoryFull(iinventory, direction)) {
				return false;
			} else {
				for (int i = 0; i < this.getSizeInventory() - 1; ++i) { // -1 because we don't want to transfer out the
																		// bottles
					if (!this.getStackInSlot(i).isEmpty()) {
						ItemStack itemstack = this.getStackInSlot(i).copy();
						ItemStack itemstack1 = HopperTileEntity.putStackInInventoryAllSlots(this, iinventory,
								this.decrStackSize(i, 1), direction);
						if (itemstack1.isEmpty()) {
							iinventory.markDirty();
							return true;
						}

						this.setInventorySlotContents(i, itemstack);
					}
				}

				return false;
			}
		}
	}

	protected boolean updateHopper(Supplier<Boolean> sup) {
		BlockPos overHopper = getPos().up();

		List<ExperienceOrbEntity> orbs = getWorld().getEntitiesWithinAABB(ExperienceOrbEntity.class,
				new AxisAlignedBB(overHopper).grow(1));
		if (this.world == null || this.world.isRemote || !this.getBlockState().get(HopperBlock.ENABLED))
			return false;

		if (!orbs.isEmpty()) {
			for (ExperienceOrbEntity orb : orbs) {
				int slot = getNextFreeBottleSlot();
				int resultXP = orb.xpValue + progress;
				ItemStack bottles = getBottles();

				if (!bottles.isEmpty()) {
					if (resultXP >= xpPerBottle && slot >= 0) {
						ItemStack output = getStackInSlot(slot);
						if (output.isEmpty())
							output = new ItemStack(Items.EXPERIENCE_BOTTLE, 1);
						else
							output.grow(1);

						bottles.shrink(1);
						setInventorySlotContents(emptyBottleSlot, bottles);
						setInventorySlotContents(slot, output);
						markDirty();
						progress = resultXP - xpPerBottle;

					} else {
						// We don't have space for a new bottle, but this orb would require a new one
						if (resultXP > xpPerBottle)
							break;

						progress = resultXP;
						orb.remove();
					}
				}
			}
		}

		if (!this.isOnTransferCooldown()) {
			boolean flag = false;

			if (!this.isEmpty()) {
				flag = this.transferItemsOut();
			}

			if (!this.isFull()) {
				flag |= sup.get();
			}

			if (flag) {
				this.setTransferCooldown(8);
				this.markDirty();
				return true;
			}
		}
		return false;
	}

	public void setTransferCooldown(int transferCooldown) {
		this.transferCooldown = transferCooldown;
	}

	private int getNextFreeBottleSlot() {
		for (int i = 0; i < hopperInventory.size() - 1; i++) {
			ItemStack s = hopperInventory.get(i);
			if (s.isEmpty() || s.getItem().equals(Items.EXPERIENCE_BOTTLE) && s.getCount() < s.getMaxStackSize())
				return i;
		}
		return -1;
	}

	ItemStack getBottles() {
		return getStackInSlot(emptyBottleSlot);
	}

	private boolean isOnTransferCooldown() {
		return this.transferCooldown > 0;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		// Only allow empty bottles into the bottle slot
		if (stack.getItem() == Items.GLASS_BOTTLE)
			return index == emptyBottleSlot;
		return true;
	}
}
