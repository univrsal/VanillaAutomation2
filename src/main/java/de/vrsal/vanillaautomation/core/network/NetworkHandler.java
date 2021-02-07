package de.vrsal.vanillaautomation.core.network;

import de.vrsal.vanillaautomation.VanillaAutomation;
import de.vrsal.vanillaautomation.core.network.messages.MessageFilterChanged;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/* Nabbed from https://github.com/Kitteh6660/MoreCraft/blob/master/java/kittehmod/morecraft/network/MorecraftPacketHandler.java */
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "2";
    public static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(VanillaAutomation.MOD_ID, "default"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    private static int index;

    public static void register()
    {
        registerMessage(MessageFilterChanged.class, MessageFilterChanged::encode, MessageFilterChanged::decode, MessageFilterChanged::handle);
    }

    private static <T> void registerMessage(Class<T> type, BiConsumer<T, PacketBuffer> encoder,
                                            Function<PacketBuffer, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> consumer)
    {
        HANDLER.registerMessage(index++, type, encoder, decoder, consumer);
    }

    public static void sendToAll(Object msg)
    {
        sendToServer(msg);
        sendToAllPlayers(msg);
    }

    public static void sendToServer(Object msg)
    {
        HANDLER.sendToServer(msg);
    }

    public static void sendToPlayer(Object msg, ServerPlayerEntity player)
    {
        if (!(player instanceof FakePlayer)) {
            HANDLER.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void sendTo(Object msg, NetworkManager netManager)
    {
        HANDLER.sendTo(msg, netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToAllPlayers(Object msg)
    {
        HANDLER.send(PacketDistributor.ALL.noArg(), msg);
    }
}
