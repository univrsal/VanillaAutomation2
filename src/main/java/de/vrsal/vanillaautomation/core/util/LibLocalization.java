package de.vrsal.vanillaautomation.core.util;

import de.vrsal.vanillaautomation.VanillaAutomation;

public class LibLocalization {
    public static final int TEXT_COLOR = 4210752;
    public static final String LABEL_META = makeGuiString("match.meta");
    public static final String LABEL_NBT = makeGuiString("match.nbt");
    public static final String LABEL_MOD = makeGuiString("match.mod");
    public static final String LABEL_FILTERS = makeGuiString("filters");

    public static String makeGuiString(String s)
    {
        return "gui." + VanillaAutomation.MOD_ID + "." + s;
    }
}
