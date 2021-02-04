package de.vrsal.vanillaautomation.core.block;

import java.util.function.Function;
import java.util.function.Supplier;

import de.vrsal.vanillaautomation.VanillaAutomation;
import de.vrsal.vanillaautomation.core.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
	  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, VanillaAutomation.MOD_ID);
	  public static final DeferredRegister<Item> ITEMS = ModItems.ITEMS;
	  
	  public static final RegistryObject<XPHopper> XP_HOPPER = register("xp_hopper", () -> new XPHopper());
	  
	  private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup) {
		  return register(name, sup, block -> item(block));
	  }

	  private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup, Function<RegistryObject<T>, Supplier<? extends Item>> itemCreator) {
	    RegistryObject<T> ret = registerNoItem(name, sup);
	    ITEMS.register(name, itemCreator.apply(ret));
	    return ret;
	  }

	  private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<? extends T> sup) {
		  return BLOCKS.register(name, sup);
	  }

	  private static Supplier<BlockItem> item(final RegistryObject<? extends Block> block) {
		  return () -> new BlockItem(block.get(), new Item.Properties().group(ItemGroup.REDSTONE));
	  }

}
