package de.vrsal.vanillaautomation.core.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class XPHopper extends HopperBlock {

	public XPHopper() {
		super(AbstractBlock.Properties.create(Material.IRON, MaterialColor.STONE).
				setRequiresTool()
				.hardnessAndResistance(3.0F, 4.8F)
				.sound(SoundType.METAL).notSolid());
	}
}
