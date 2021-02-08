package de.vrsal.vanillaautomation.client.screen.widgets;

import net.minecraft.util.IIntArray;
import net.minecraft.util.text.TranslationTextComponent;

public class ButtonCheckbox extends ButtonCycleIcon {

    public ButtonCheckbox(String unlocalizedText, int x, int y, int fieldId, IIntArray fields, IPressable action) {
        super(x, y, new TranslationTextComponent(unlocalizedText), fieldId, fields, IconType.UNCHECKED, action);
    }

    public boolean isChecked() {
        return fields.get(this.fieldId) != 0;
    }
}