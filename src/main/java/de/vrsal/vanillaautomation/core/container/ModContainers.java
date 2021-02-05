package de.vrsal.vanillaautomation.core.container;

import de.vrsal.vanillaautomation.VanillaAutomation;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {
	public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, VanillaAutomation.MOD_ID);

	public static final RegistryObject<ContainerType<XPHopperContainer>> XP_HOPPER = CONTAINERS.register("xp_hopper",
			() -> new ContainerType<>(XPHopperContainer::createContainer));

}
