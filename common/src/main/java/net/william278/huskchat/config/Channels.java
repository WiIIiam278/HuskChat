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

package net.william278.huskchat.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.william278.huskchat.channel.Channel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class for loading and storing {@link Channel}s
 */
@SuppressWarnings("FieldMayBeFinal")
@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Channels {

    static final String CONFIG_HEADER = """
            ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
            ┃      HuskChat - Channels     ┃
            ┃    Developed by William278   ┃
            ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
            ┣╸ Information: https://william278.net/project/huskchat/
            ┗╸ Channels Help: https://william278.net/docs/huskchat/channels/""";

    @Comment("The default chat channel players are placed in (can be overridden by server_default_channels)")
    private String defaultChannel = "global";

    @Comment("Map of server names to a channel players will be automatically moved into when they join that server")
    private Map<String, String> serverDefaultChannels = Map.of("example", "global");

    @Comment("The format of log messages (applies to channels with logging enabled)")
    private String channelLogFormat = "[CHAT] [%channel%] %sender%: ";

    @Comment("Aliases for the /channel command")
    @Getter(AccessLevel.NONE)
    private List<String> channelCommandAliases = List.of("channel", "c");

    @Comment("Channel definitions")
    private List<Channel> channels = List.of(
            // Local channel
            Channel.builder()
                    .id("local")
                    .format("%fullname%&r&f: ")
                    .broadcastScope(Channel.BroadcastScope.LOCAL)
                    .shortcutCommands(List.of("/local", "/l"))
                    .build(),

            // Global channel
            Channel.builder()
                    .id("global")
                    .format("&#00fb9a&[G]&r&f %fullname%&r&f: ")
                    .broadcastScope(Channel.BroadcastScope.GLOBAL)
                    .shortcutCommands(List.of("/global", "/g"))
                    .build(),

            // Staff channel
            Channel.builder()
                    .id("staff")
                    .format("&e[Staff] %name%: &7")
                    .broadcastScope(Channel.BroadcastScope.GLOBAL)
                    .filtered(false)
                    .permissions(Channel.ChannelPermissions.builder()
                            .send("huskchat.channel.staff.send")
                            .receive("huskchat.channel.staff.receive")
                            .build())
                    .shortcutCommands(List.of("/staff", "/sc"))
                    .build(),

            // HelpOp channel
            Channel.builder()
                    .id("helpop")
                    .format("&#00fb9a&[HelpOp] %name%:&7")
                    .broadcastScope(Channel.BroadcastScope.GLOBAL)
                    .filtered(false)
                    .permissions(Channel.ChannelPermissions.builder()
                            .receive("huskchat.channel.helpop.receive")
                            .build())
                    .shortcutCommands(List.of("/helpop", "/helpme"))
                    .build()
    );

    public Optional<Channel> getChannel(@Nullable String channelId) {
        if (channelId == null) {
            return Optional.empty();
        }
        return channels.stream().filter(channel -> channel.getId().equalsIgnoreCase(channelId)).findFirst();
    }

    @NotNull
    public List<String> getChannelCommandAliases() {
        return Settings.formatCommands(channelCommandAliases);
    }

}
