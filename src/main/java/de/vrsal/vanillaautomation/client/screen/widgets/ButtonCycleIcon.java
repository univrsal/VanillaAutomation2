package de.vrsal.vanillaautomation.client.screen.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ButtonCycleIcon extends Button {
    protected int fieldId = 0;
    protected IIntArray fields;
    protected List<IconType> icons;
    protected boolean isEnabled = true;
    protected Color textColor;

    public ButtonCycleIcon(int x, int y, ITextComponent title, int fieldId, IIntArray fields, List<IconType> icons, IPressable pressedAction) {
        super(x, y, 0, 0, title, pressedAction);
        this.icons = icons;
        this.fieldId = fieldId;
        this.fields = fields;
        this.textColor = new Color(55, 55, 55);
        this.height = Minecraft.getInstance().fontRenderer.FONT_HEIGHT;
        this.width = Minecraft.getInstance().fontRenderer.getStringWidth(getMessage().getString()) + icons.get(0).getWidth() + 3;
    }

    public ButtonCycleIcon(int x, int y, ITextComponent title, int fieldId, IIntArray fields, IconType defaultIcon, IPressable pressedAction) {
        this(x, y, title, fieldId, fields, Arrays.asList(defaultIcon, defaultIcon.toggle()), pressedAction);
    }

    public ButtonCycleIcon(int x, int y, int fieldId, IIntArray fields, IconType defaultIcon, IPressable pressedAction) {
        this(x, y, new StringTextComponent(""), fieldId, fields, Arrays.asList(defaultIcon, defaultIcon.toggle()), pressedAction);
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int offset = 0;
        IconType icon = icons.get(getState());
        offset = icon.getWidth() + 3;
        icon.draw(this, matrixStack, true);
        Minecraft mc = Minecraft.getInstance();

        if (visible) {
            int color = 0;
            if (isEnabled) {
                color = isMouseOver(mouseX, mouseY) ? 16777120 : textColor.getRGB();
            } else {
                color = 10526880;
            }
            mc.fontRenderer.drawString(matrixStack, getMessage().getString(), x + offset, y, color);
        }

        if (this.isHovered()) {
            this.renderToolTip(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public void onPress() {
        int val = getState();
        val = (++val) % icons.size();
        fields.set(this.fieldId, val);
        super.onPress();
    }

    public int getState() {
        return fields.get(this.fieldId);
    }
}