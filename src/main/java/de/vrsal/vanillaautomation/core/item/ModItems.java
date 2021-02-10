package de.vrsal.vanillaautomation.core.item;

import de.vrsal.vanillaautomation.VanillaAutomation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
			VanillaAutomation.MOD_ID);
	public static final RegistryObject<Item> XP_HOPPER_CART = ITEMS.register("xp_hopper_cart",
			XPHopperCartItem::new);
	public static final RegistryObject<Item> FILTERED_HOPPER_CART = ITEMS.register("filtered_hopper_cart",
			FilteredHopperCartItem::new);
}
