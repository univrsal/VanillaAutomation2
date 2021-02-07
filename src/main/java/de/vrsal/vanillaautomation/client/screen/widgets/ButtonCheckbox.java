package de.vrsal.vanillaautomation.client.screen.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;

public class ButtonCheckbox extends Button {

    private Color textColor;
    private IconType icon;
    private boolean isChecked = false;
    private boolean isEnabled = true;

    public ButtonCheckbox(String unlocalizedText, int x, int y, boolean checked, IPressable action) {
        super(x, y, 0, 0, new TranslationTextComponent(unlocalizedText), action);
        textColor = new Color(55, 55, 55);
        this.isChecked  = checked;
        height = Minecraft.getInstance().fontRenderer.FONT_HEIGHT;
        icon = checked ? IconType.CHECKED : IconType.UNCHECKED;
        width = Minecraft.getInstance().fontRenderer.getStringWidth(getMessage().getString()) + icon.getWidth() + 3;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int offset = 0;
        if (icon != null) {
            offset = icon.getWidth() + 3;
            icon.draw(this, matrixStack, true);
        }
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
        setIcon(icon.toggle());
        isChecked = !isChecked;
        super.onPress();
    }

    public boolean isChecked() {
        return isChecked;
    }

    public IconType getIcon() {
        return icon;
    }

    public void setIcon(IconType icon) {
        this.icon = icon;
    }
}