package de.vrsal.vanillaautomation.core.block.tileentity;

import net.minecraft.tileentity.IHopper;

public interface IFilteredHopper extends IHopper {
    boolean whitelist();
    boolean matchMeta();
    boolean matchMod();
    boolean matchNBT();

    void setWhitelist(boolean b);
    void setMatchMeta(boolean b);
    void setMatchMod(boolean b);
    void setMatchNBT(boolean b);

    int getFirstFilterSlot();
}
