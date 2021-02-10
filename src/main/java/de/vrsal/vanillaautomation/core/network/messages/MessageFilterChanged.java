package de.vrsal.vanillaautomation.core.network.messages;

import de.vrsal.vanillaautomation.VanillaAutomation;
import de.vrsal.vanillaautomation.core.block.tileentity.IFilteredHopper;
import de.vrsal.vanillaautomation.core.block.tileentity.TileFilteredHopper;
import de.vrsal.vanillaautomation.core.container.BaseContainer;
import de.vrsal.vanillaautomation.core.container.FilteredHopperContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class MessageFilterChanged {
    private final boolean whitelist, matchMeta, matchNBT, matchMod;
    public MessageFilterChanged(boolean whitelist, boolean matchMeta, boolean matchNBT, boolean matchMod)
    {
        this.whitelist = whitelist;
        this.matchMeta = matchMeta;
        this.matchMod = matchMod;
        this.matchNBT = matchNBT;
    }

    public static void encode(MessageFilterChanged message, PacketBuffer buf)
    {
        buf.writeBoolean(message.whitelist);
        buf.writeBoolean(message.matchMeta);
        buf.writeBoolean(message.matchNBT);
        buf.writeBoolean(message.matchMod);
    }

    public static MessageFilterChanged decode(PacketBuffer buf)
    {
        return new MessageFilterChanged(buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean());
    }

    public static void handle(final MessageFilterChanged msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
                IFilteredHopper hopper = null;
                Container openContainer = Objects.requireNonNull(ctx.get().getSender()).openContainer;
                if (openContainer instanceof BaseContainer) {
                    IInventory tileInventory = ((BaseContainer) openContainer).getTileInventory();
                    if (tileInventory instanceof IFilteredHopper)
                        hopper = (IFilteredHopper) tileInventory;
                }

                if (hopper != null) {
                    hopper.setMatchNBT(msg.matchNBT);
                    hopper.setMatchMeta(msg.matchMeta);
                    hopper.setMatchMod(msg.matchMod);
                    hopper.setWhitelist(msg.whitelist);
                }
            }
        });
    }
}
