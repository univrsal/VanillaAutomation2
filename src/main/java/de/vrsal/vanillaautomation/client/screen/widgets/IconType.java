package de.vrsal.vanillaautomation.client.screen.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.vrsal.vanillaautomation.VanillaAutomation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;

public enum IconType {
    WHITELIST(0, 0),
    BLACKLIST(16, 0),
    UNCHECKED(32, 0),
    CHECKED(48, 0),
    ARROW_RIGHT(64, 0, 10, 15),
    ARROW_LEFT(74, 0, 10, 15),
    ARROW_RIGHT_SELECTED(84, 0, 10, 15),
    ARROW_LEFT_SELECTED(94, 0, 10, 15),
    ARROW_RIGHT_DISABLED(104, 0, 10, 15),
    ARROW_LEFT_DISABLED(114, 0, 10, 15);

    private int x, y, width, height;

    private static final ResourceLocation ICON_TEXTURE = new ResourceLocation(VanillaAutomation.MOD_ID, "textures/gui/icons.png");

    IconType(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 16;
    }

    IconType(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public void draw(Button parent, MatrixStack ms, boolean drawDefaultTexture) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(ICON_TEXTURE);
        if (drawDefaultTexture) {
            int xPos = parent.x + 2;
            if (parent.getMessage().getString().equals(""))
                xPos = parent.x + parent.getWidth() / 2 - this.width / 2;
            int yPos = parent.y + parent.getHeightRealms() / 2 - this.width / 2;
            parent.blit(ms, xPos, yPos, x, y, width, height);
        } else {
            parent.blit(ms, parent.x, parent.y, x, y, width, height);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public IconType toggle() {
        if (ordinal() % 2 == 0)
            return IconType.values()[ordinal() + 1];
        else
            return IconType.values()[ordinal() - 1];
    }
}