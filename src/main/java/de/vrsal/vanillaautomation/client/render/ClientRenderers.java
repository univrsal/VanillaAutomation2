package de.vrsal.vanillaautomation.client.render;

import de.vrsal.vanillaautomation.core.entity.ModEntities;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientRenderers {

    @OnlyIn(Dist.CLIENT)
    public static void setup() {
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.XP_HOPPER_CART.get(), (IRenderFactory<AbstractMinecartEntity>) MinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.FILTERED_HOPPER_CART.get(), (IRenderFactory<AbstractMinecartEntity>) MinecartRenderer::new);
    }
}
