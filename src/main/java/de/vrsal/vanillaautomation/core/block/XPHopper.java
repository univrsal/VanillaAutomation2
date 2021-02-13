package de.vrsal.vanillaautomation.core.block;

import de.vrsal.vanillaautomation.core.block.tileentity.TileXPHopper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class XPHopper extends HopperBlock {

	public XPHopper() {
		super(AbstractBlock.Properties.create(Material.IRON, MaterialColor.STONE).setRequiresTool()
				.hardnessAndResistance(3.0F, 4.8F).sound(SoundType.METAL).notSolid());
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileXPHopper();
	}

	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isRemote) {
			return ActionResultType.SUCCESS;
		} else {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof TileXPHopper) {
				player.openContainer((TileXPHopper) tileentity);
				player.addStat(Stats.INSPECT_HOPPER);
			}
			return ActionResultType.CONSUME;
		}
	}
	
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.isIn(newState.getBlock())) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof TileXPHopper) {
				InventoryHelper.dropInventoryItems(worldIn, pos, (TileXPHopper) tileentity);
				worldIn.updateComparatorOutputLevel(pos, this);
			}

			super.onReplaced(state, worldIn, pos, newState, isMoving);
		}
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (stack.hasDisplayName()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof TileXPHopper) {
				((TileXPHopper)tileentity).setCustomName(stack.getDisplayName());
			}
		}
	}
}
