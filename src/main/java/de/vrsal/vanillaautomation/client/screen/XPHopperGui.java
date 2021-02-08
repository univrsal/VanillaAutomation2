package de.vrsal.vanillaautomation.client.screen;

import java.text.DecimalFormat;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.vrsal.vanillaautomation.VanillaAutomation;
import de.vrsal.vanillaautomation.core.block.tileentity.TileXPHopper;
import de.vrsal.vanillaautomation.core.container.XPHopperContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.BeaconScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class XPHopperGui extends ContainerScreen<XPHopperContainer> implements IHasContainer<XPHopperContainer> {

	private static ResourceLocation texture = new ResourceLocation(VanillaAutomation.MOD_ID,
			"textures/gui/xp_hopper.png");

	private IInventory playerInventory;
	private XPHopperContainer container;

	public XPHopperGui(XPHopperContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		this.passEvents = false;
		this.ySize = 133;
		this.playerInventoryTitleY = this.ySize - 94;
		this.playerInventory = inv;
		this.container = screenContainer;
	}

	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		
		// percent tool tip
		float percentage = ((float) container.getProgress()) / TileXPHopper.xpPerBottle;
		if (x + 128 <= mouseX && mouseX <= x + 132 &&
				y + 20 <= mouseY && mouseY <= y + 20 + 16 && Minecraft.getInstance().player.inventory.getItemStack().isEmpty())
		{
			renderTooltip(matrixStack, new StringTextComponent((int)(percentage * 100) + "%"),
					mouseX - this.guiLeft, mouseY - this.guiTop);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX,
			int mouseY) {

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(XPHopperGui.texture);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		blit(matrixStack, x, y, 0, 0, this.xSize, this.ySize, 256, 256);

		float percentage = ((float) container.getProgress()) / TileXPHopper.xpPerBottle;
		int progress = (int) (percentage * 16);

		blit(matrixStack, x + 129, y + 20 + 16 - progress, 177, 17 - progress, 2, progress);
		
	}

}
