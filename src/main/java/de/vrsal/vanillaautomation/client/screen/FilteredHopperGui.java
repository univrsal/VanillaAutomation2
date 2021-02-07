package de.vrsal.vanillaautomation.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.vrsal.vanillaautomation.VanillaAutomation;
import de.vrsal.vanillaautomation.client.screen.widgets.ButtonCheckbox;
import de.vrsal.vanillaautomation.core.block.tileentity.TileFilteredHopper;
import de.vrsal.vanillaautomation.core.block.tileentity.TileXPHopper;
import de.vrsal.vanillaautomation.core.container.FilteredHopperContainer;
import de.vrsal.vanillaautomation.core.network.NetworkHandler;
import de.vrsal.vanillaautomation.core.network.messages.MessageFilterChanged;
import de.vrsal.vanillaautomation.core.util.LibLocalization;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FilteredHopperGui extends ContainerScreen<FilteredHopperContainer> implements IHasContainer<FilteredHopperContainer> {
    private static ResourceLocation texture = new ResourceLocation(VanillaAutomation.MOD_ID,
            "textures/gui/filtered_hopper.png");

    private IInventory playerInventory;
    private FilteredHopperContainer container;

    private ButtonCheckbox btnMatchMeta;
    private ButtonCheckbox btnMatchNBT;
    private ButtonCheckbox btnMatchMod;
    private int xOffset = 0;
    private TranslationTextComponent filterText;

    public FilteredHopperGui(FilteredHopperContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.passEvents = false;
        this.ySize = 153;
        this.playerInventoryTitleY = this.ySize - 94;
        this.playerInventory = inv;
        this.container = screenContainer;
        this.filterText = new TranslationTextComponent(LibLocalization.LABEL_FILTERS);
    }

    boolean getField(int id) {
        return container.fields.get(id) != 0;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        super.drawGuiContainerForegroundLayer(matrixStack, x, y);
        font.drawString(matrixStack, filterText.getString(), titleX, 45, LibLocalization.TEXT_COLOR);
    }

    @Override
    protected void init() {
        super.init();
//        btnIco = new ButtonIcon(0, guiLeft + 136, guiTop + 38, ButtonIcon.IconType.values()[hopperInventory.getField(0)]);
        xOffset = minecraft.fontRenderer.getStringWidth(I18n.format(LibLocalization.LABEL_MOD)) + 21;

        Button.IPressable onPress = btn -> {
            NetworkHandler.sendToServer(new MessageFilterChanged(false, btnMatchMeta.isChecked(),
                    btnMatchNBT.isChecked(), btnMatchMod.isChecked()));
        };

        btnMatchMeta = addButton(new ButtonCheckbox(LibLocalization.LABEL_META, guiLeft - xOffset, guiTop + 12, getField(TileFilteredHopper.META), onPress));
        btnMatchNBT = addButton(new ButtonCheckbox(LibLocalization.LABEL_NBT, guiLeft - xOffset, guiTop + 24, getField(TileFilteredHopper.NBT), onPress));
        btnMatchMod = addButton(new ButtonCheckbox(LibLocalization.LABEL_MOD, guiLeft - xOffset, guiTop + 36, getField(TileFilteredHopper.MOD), onPress));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX,
                                                   int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(texture);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        blit(matrixStack, x, y, 0, 0, this.xSize, this.ySize, 256, 256);
        // filter settings
        blit(matrixStack, guiLeft - xOffset, guiTop + 5, 0, this.ySize, xOffset, 58);
    }

}
