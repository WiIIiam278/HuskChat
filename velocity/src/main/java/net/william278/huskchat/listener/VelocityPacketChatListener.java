/*
 * This file is part of HuskChat, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.huskchat.listener;

import com.google.common.collect.Sets;
import com.velocitypowered.api.event.AwaitingEventExecutor;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.network.Connections;
import com.velocitypowered.proxy.protocol.packet.chat.keyed.KeyedPlayerChatPacket;
import com.velocitypowered.proxy.protocol.packet.chat.legacy.LegacyChatPacket;
import com.velocitypowered.proxy.protocol.packet.chat.session.SessionPlayerChatPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.william278.desertwell.util.ThrowingConsumer;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.VelocityHuskChat;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class VelocityPacketChatListener {
    private static final String KEY = "huskchat";

    private final VelocityHuskChat plugin;
    @Getter
    private final Set<UUID> huskChatEntries;

    public VelocityPacketChatListener(@NotNull VelocityHuskChat plugin) {
        this.plugin = plugin;
        this.huskChatEntries = Sets.newConcurrentHashSet();
    }

    public void register() {
        this.loadPlayers();
        this.loadListeners();
    }

    private void loadPlayers() {
        plugin.getServer().getAllPlayers().forEach(this::injectPlayer);
    }

    private void loadListeners() {
        plugin.getServer().getEventManager().register(plugin, PostLoginEvent.class,
                (AwaitingEventExecutor<PostLoginEvent>) postLoginEvent -> EventTask.withContinuation(continuation -> {
                    injectPlayer(postLoginEvent.getPlayer());
                    continuation.resume();
                }));

        plugin.getServer().getEventManager().register(plugin, DisconnectEvent.class,
                (AwaitingEventExecutor<DisconnectEvent>) disconnectEvent ->
                        disconnectEvent.getLoginStatus() == DisconnectEvent.LoginStatus.CONFLICTING_LOGIN
                                ? null
                                : EventTask.async(() -> removePlayer(disconnectEvent.getPlayer())));
    }

    public void injectPlayer(@NotNull Player player) {
        final PlayerChannelHandler handler = new PlayerChannelHandler(plugin, player);
        final ConnectedPlayer connectedPlayer = (ConnectedPlayer) player;
        removePlayer(player);
        connectedPlayer.getConnection()
                .getChannel()
                .pipeline()
                .addBefore(Connections.HANDLER, KEY, handler);
    }

    public void removePlayer(@NotNull Player player) {
        final ConnectedPlayer connectedPlayer = (ConnectedPlayer) player;
        final Channel channel = connectedPlayer.getConnection().getChannel();
        if (channel.pipeline().get(KEY) != null) {
            channel.pipeline().remove(KEY);
        }
    }

    @RequiredArgsConstructor
    public static class PlayerChannelHandler extends ChannelDuplexHandler implements VelocityChatListener {

        private static final String LEGACY_COMMAND_PREFIX = "/";
        private final VelocityHuskChat plugin;
        private final Player player;

        @Override
        public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object packet) throws Exception {
            final Optional<String> message = this.extractChatMessage(packet);
            if (message.isEmpty()) {
                super.channelRead(ctx, packet);
                return;
            }
            this.handleChat(message.get(), (passthrough) -> super.channelRead(ctx, packet));
        }

        @NotNull
        private Optional<String> extractChatMessage(Object msg) {
            if (msg instanceof final SessionPlayerChatPacket session) {
                // Handle session chat (1.19.4+)
                return Optional.of(session.getMessage());
            } else if (msg instanceof final KeyedPlayerChatPacket keyed) {
                // Handle keyed chat (1.19.2-4)
                return Optional.of(keyed.getMessage());
            } else if (msg instanceof final LegacyChatPacket legacy) {
                // Handle legacy chat (pre-1.19.1)
                if (legacy.getMessage().startsWith(LEGACY_COMMAND_PREFIX)) {
                    return Optional.empty();
                }
                return Optional.of(legacy.getMessage());
            }
            return Optional.empty();
        }

        private void handleChat(@NotNull String message, @NotNull ThrowingConsumer<Void> ifAllowed) {
            this.dispatchEvent(message)
                    .thenApply(event -> event.getResult().isAllowed() && handlePlayerChat(event))
                    .thenAccept(allowed -> {
                        if (allowed) {
                            ifAllowed.accept(null);
                        }
                    });
        }

        @NotNull
        private CompletableFuture<PlayerChatEvent> dispatchEvent(@NotNull String message) {
            return plugin.getServer().getEventManager().fire(new PlayerChatEvent(player, message));
        }

        @Override
        @NotNull
        public HuskChat plugin() {
            return plugin;
        }
    }

}
