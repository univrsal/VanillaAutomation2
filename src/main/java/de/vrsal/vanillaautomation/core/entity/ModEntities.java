package de.vrsal.vanillaautomation.core.entity;

import de.vrsal.vanillaautomation.VanillaAutomation;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, VanillaAutomation.MOD_ID);

    public static final RegistryObject<EntityType<XPHopperCartEntity>> XP_HOPPER_CART = ENTITIES.register("xp_hopper_cart",
            () -> EntityType.Builder.<XPHopperCartEntity>create(XPHopperCartEntity::new, EntityClassification.MISC).
                    size(0.98F, 0.7F).trackingRange(8).setCustomClientFactory((spawnEntity, world) -> new XPHopperCartEntity(world,
                    0, 0, 0)).build("xp_hopper_cart"));

    public static final RegistryObject<EntityType<FilteredHopperCartEntity>> FILTERED_HOPPER_CART = ENTITIES.register("filtered_hopper_cart",
            () -> EntityType.Builder.<FilteredHopperCartEntity>create(FilteredHopperCartEntity::new, EntityClassification.MISC).
                    size(0.98F, 0.7F).trackingRange(8).setCustomClientFactory((spawnEntity, world) -> new FilteredHopperCartEntity(world,
                    0, 0, 0)).build("filtered_hopper_cart"));
}
