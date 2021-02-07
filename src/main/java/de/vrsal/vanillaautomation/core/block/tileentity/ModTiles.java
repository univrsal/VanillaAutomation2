package de.vrsal.vanillaautomation.core.block.tileentity;

import de.vrsal.vanillaautomation.VanillaAutomation;
import de.vrsal.vanillaautomation.core.block.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.google.common.collect.Sets;

public class ModTiles {
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, VanillaAutomation.MOD_ID);

	  public static final RegistryObject<TileEntityType<TileXPHopper>> XP_HOPPER = TILE_ENTITIES.register(
	          "xp_hopper", () -> new TileEntityType<>(TileXPHopper::new, Sets.newHashSet(ModBlocks.XP_HOPPER.get()), null));
	public static final RegistryObject<TileEntityType<TileXPHopper>> FILTERED_HOPPER = TILE_ENTITIES.register(
			"filtered_hopper", () -> new TileEntityType<>(TileXPHopper::new, Sets.newHashSet(ModBlocks.FILTERED_HOPPER.get()), null));
}
