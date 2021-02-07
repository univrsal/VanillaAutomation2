package de.vrsal.vanillaautomation.core.network.messages;

import de.vrsal.vanillaautomation.VanillaAutomation;
import de.vrsal.vanillaautomation.core.block.tileentity.TileFilteredHopper;
import de.vrsal.vanillaautomation.core.container.FilteredHopperContainer;
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
                TileFilteredHopper te = null;
                Container openContainer = Objects.requireNonNull(ctx.get().getSender()).openContainer;
                if (openContainer instanceof FilteredHopperContainer) {
                    te = ((FilteredHopperContainer) ctx.get().getSender().openContainer).getTile();
                }

                if (te != null) {
                    te.setMatchNBT(msg.matchNBT);
                    te.setMatchMeta(msg.matchMeta);
                    te.setMatchMod(msg.matchMod);
                    te.setWhitelist(msg.whitelist);
                }
            }
        });
    }
}
