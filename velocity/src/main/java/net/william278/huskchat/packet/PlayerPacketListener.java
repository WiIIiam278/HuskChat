package net.william278.huskchat.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketConfigSendEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.chat.ChatTypes;
import com.github.retrooper.packetevents.protocol.chat.LastSeenMessages;
import com.github.retrooper.packetevents.protocol.chat.MessageSignature;
import com.github.retrooper.packetevents.protocol.chat.filter.FilterMask;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_19_1;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_19_3;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.crypto.MessageSignData;
import com.github.retrooper.packetevents.wrapper.configuration.server.WrapperConfigServerRegistryData;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import net.william278.huskchat.VelocityHuskChat;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerPacketListener extends SimplePacketListenerAbstract {

    private static final int MAX_SEEN = 20;

    private final VelocityHuskChat plugin;
    private final PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();
    private final Map<UUID, List<LastMessage>> seenMessages = Maps.newConcurrentMap();
    private final List<LastMessage> allMessages = Lists.newArrayList();

    public PlayerPacketListener(@NotNull VelocityHuskChat plugin) {
        this.plugin = plugin;
        ChatTypes.define(RegistryEditor.HUSKCHAT.getName().asString());
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CHAT_MESSAGE) {
            try {
                final WrapperPlayClientChatMessage packet = new WrapperPlayClientChatMessage(event);
                this.sendSignedMessage(event.getUser(), packet);
                event.setCancelled(true);
            } catch (Throwable e) {
                plugin.log(Level.SEVERE, "Failed to handle CHAT_MESSAGE packet", e);
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CHAT_ACK) {
            try {
                final ByteBuf byteBuf = (ByteBuf) event.getByteBuf();
                seenMessages.getOrDefault(event.getUser().getUUID(), Lists.newArrayList())
                        .add(allMessages.get(byteBuf.readInt()));
                plugin.log(Level.INFO, "Received CHAT_ACK packet");
                event.setCancelled(true);
            } catch (Throwable e) {
                plugin.log(Level.SEVERE, "Failed to handle CHAT_ACK packet", e);
            }
        }
    }

    @Override
    public void onPacketConfigSend(PacketConfigSendEvent event) {
        if (event.getPacketType() == PacketType.Configuration.Server.REGISTRY_DATA) {
            try {
                final WrapperConfigServerRegistryData wrapper = new WrapperConfigServerRegistryData(event);
                wrapper.setRegistryData(injectChatTypes(wrapper.getRegistryData()));
                event.markForReEncode(true);

                // Initialize the user's message history
                final UUID uuid = event.getUser().getUUID();
                seenMessages.put(uuid, Lists.newArrayList());
            } catch (Throwable e) {
                plugin.log(Level.SEVERE, "Failed to handle SERVERBOUND_REGISTRY_SYNC packet", e);
            }
        }
    }

    @NotNull
    private NBTCompound injectChatTypes(@NotNull NBTCompound data) {
        final RegistryEditor wrapper = new RegistryEditor(data);
        wrapper.injectTypes(List.of(RegistryEditor.HUSKCHAT));
        return wrapper.root();
    }

    private void sendSignedMessage(@NotNull User sender, @NotNull WrapperPlayClientChatMessage packet) {
        if (packet.getLastSeenMessages() == null) {
            throw new IllegalStateException("LastSeenMessages is null");
        }

        (plugin.getPlayer(sender.getUUID())).get().sendMessage(Component.text(
                "Offset: " + packet.getLastSeenMessages().getOffset() + "   "
                        + packet.getLastSeenMessages().getAcknowledged().toString()
        ));

        int messagesSinceILastSentOne = packet.getLastSeenMessages().getOffset();
        int messagesSinceILastSaw = packet.getLastSeenMessages().getAcknowledged().size();

        // todo isAllowed
        try {
            plugin.log(Level.INFO, "Sending signed message 1");
            final MessageSignData sign = packet.getMessageSignData().orElse(null);
            if (sign == null) {
                plugin.log(Level.WARNING, "Failed to send signed message (no sign data)");
                return;
            }
            plugin.log(Level.INFO, "Sending signed message 2");

            System.out.println("SENDING: " + UUID.nameUUIDFromBytes(sign.getSaltSignature().getSignature()).toString().split("-")[0]);

            ChatMessage_v1_19_3 message = new ChatMessage_v1_19_3(
                    sender.getUUID(),
                    0,
                    sign.getSaltSignature().getSignature(),
                    packet.getMessage(),
                    sign.getTimestamp(),
                    sign.getSaltSignature().getSalt(),
                    null,
                    null,
                    FilterMask.PASS_THROUGH,
                    new ChatMessage_v1_19_1.ChatTypeBoundNetwork(
//                            ChatTypes.getByName(RegistryEditor.HUSKCHAT.getName().asString()),
                            ChatTypes.CHAT,
                            Component.text(sender.getName()), //todo chat message format goes here
                            null
                    )
            );
            plugin.log(Level.INFO, "Sending signed message 3");
            final LastMessage thisMessage = new LastMessage(sender.getUUID(), sign.getSaltSignature().getSignature());
            plugin.getProxyServer().getAllPlayers().forEach(receiver -> {
                final List<LastMessage> seen = seenMessages.getOrDefault(receiver.getUniqueId(), Lists.newArrayList());
                playerManager.sendPacket(receiver, getChatPacket(message, seen));
                seen.add(thisMessage);
            });
            allMessages.add(0, thisMessage);

        } catch (Throwable e) {
            plugin.log(Level.SEVERE, "Failed to dispatch packet (unsupported client or server version)", e);
        }
    }

    @NotNull
    private WrapperPlayServerChatMessage getChatPacket(@NotNull ChatMessage_v1_19_3 message, @NotNull List<LastMessage> seen) {
        message.setIndex((int) seen.stream().filter(s -> s.sender.equals(message.getSenderUUID())).count());
        message.setLastSeenMessagesPacked(LastMessage.pack(allMessages, seen));
        return new WrapperPlayServerChatMessage(message);
    }

    private record LastMessage(UUID sender, byte[] signature) {

        // Pack the last MAX_SEEN messages into an Array of LastSeenMessages.Packed
        @NotNull
        private static LastSeenMessages.Packed pack(@NotNull List<LastMessage> all, @NotNull List<LastMessage> seen) {
            final List<MessageSignature.Packed> packed = Lists.newArrayList();
            for (int i = Math.min(all.size() - 1, MAX_SEEN - 1); i >= 0; i--) {
                final LastMessage message = all.get(i);
                if (packed.isEmpty() && !seen.contains(message)) {
                    continue;
                }
                packed.add(seen.contains(message)
                        ? new MessageSignature.Packed(i)
                        : new MessageSignature.Packed(new MessageSignature(message.signature)));
                System.out.println("SENT: " + UUID.nameUUIDFromBytes(message.signature).toString().split("-")[0]);
            }
            return new LastSeenMessages.Packed(packed);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof LastMessage message && message.sender.equals(sender)) {
                return Arrays.equals(message.signature, signature);
            }
            return false;
        }

    }

}