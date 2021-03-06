package de.vrsal.vanillaautomation;

import de.vrsal.vanillaautomation.client.render.ClientRenderers;
import de.vrsal.vanillaautomation.client.screen.FilteredHopperGui;
import de.vrsal.vanillaautomation.core.network.NetworkHandler;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.vrsal.vanillaautomation.client.screen.XPHopperGui;
import de.vrsal.vanillaautomation.core.block.ModBlocks;
import de.vrsal.vanillaautomation.core.block.tileentity.ModTiles;
import de.vrsal.vanillaautomation.core.container.ModContainers;
import de.vrsal.vanillaautomation.core.entity.ModEntities;
import de.vrsal.vanillaautomation.core.item.ModItems;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(VanillaAutomation.MOD_ID)
public class VanillaAutomation
{
	public static final String MOD_ID = "vanillaautomation";

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public VanillaAutomation() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> { FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff); });

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModTiles.TILE_ENTITIES.register(modBus);
        ModContainers.CONTAINERS.register(modBus);
        ModEntities.ENTITIES.register(modBus);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        NetworkHandler.register();
    }
    
    @OnlyIn(Dist.CLIENT)
    private void doClientStuff(final FMLClientSetupEvent event)
    {
        ClientRenderers.setup();
    	ScreenManager.registerFactory(ModContainers.XP_HOPPER.get(), XPHopperGui::new);
        ScreenManager.registerFactory(ModContainers.FILTERED_HOPPER.get(), FilteredHopperGui::new);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event)
    {

    }

}
