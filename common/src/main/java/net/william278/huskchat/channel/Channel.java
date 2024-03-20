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

package net.william278.huskchat.channel;

import de.exlll.configlib.Configuration;
import lombok.*;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder
@Getter
@Setter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Channel {

    private String id;

    @Builder.Default
    private String format = "<%sender%> ";

    private BroadcastScope broadcastScope;

    @Builder.Default
    private boolean logToConsole = true;

    @Builder.Default
    private List<String> restrictedServers = new ArrayList<>();

    @Builder.Default
    private boolean filtered = true;

    @Builder.Default
    private ChannelPermissions permissions = new ChannelPermissions();

    @Builder.Default
    @Getter(AccessLevel.NONE)
    private List<String> shortcutCommands = new ArrayList<>();

    @Builder
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Setter
    public static class ChannelPermissions {
        @Nullable
        @Builder.Default
        private String send = null;

        @Nullable
        @Builder.Default
        private String receive = null;

        public Optional<String> getSend() {
            return Optional.ofNullable(send);
        }

        public Optional<String> getReceive() {
            return Optional.ofNullable(receive);
        }
    }

    /**
     * The broadcast scope of a channel, defining how messages will be handled
     */
    @Getter
    @AllArgsConstructor
    public enum BroadcastScope {
        /**
         * The message is broadcast globally to those with permissions via the proxy
         */
        GLOBAL(false),

        /**
         * The message is broadcast via the proxy to players who have permission and are on
         * the same server as the source
         */
        LOCAL(false),

        /**
         * The message is not handled by the proxy and is instead passed to the backend server
         */
        PASSTHROUGH(true),

        /**
         * The message is broadcast via the proxy to players who have permission and are on the same server
         * as the source and is additionally passed to the backend server
         */
        LOCAL_PASSTHROUGH(true),

        /**
         * The message is broadcast globally to those with permissions via the proxy
         * and is additionally passed to the backend server
         */
        GLOBAL_PASSTHROUGH(true);

        private final boolean passThrough;

        public boolean isOneOf(BroadcastScope... scopes) {
            for (BroadcastScope scope : scopes) {
                if (this == scope) {
                    return true;
                }
            }
            return false;
        }
    }

    @NotNull
    public List<String> getShortcutCommands() {
        return Settings.formatCommands(shortcutCommands);
    }

    public boolean isServerRestricted(@NotNull String server) {
        return restrictedServers.stream().anyMatch(server::equalsIgnoreCase);
    }

    public boolean canUserSend(@NotNull OnlineUser user) {
        return permissions.getSend().map(node -> user.hasPermission(node, false)).orElse(true);
    }

    public boolean canUserReceive(@NotNull OnlineUser user) {
        return permissions.getReceive().map(node -> user.hasPermission(node, false)).orElse(true);
    }

}
