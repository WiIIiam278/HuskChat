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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Channel {

    private final String id;
    private final String format;
    private final BroadcastScope broadcastScope;
    private List<String> shortcutCommands = new ArrayList<>();
    private List<String> restrictedServers = new ArrayList<>();
    private String sendPermission;
    private String receivePermission;
    private boolean logMessages;
    private boolean filter;

    /**
     * Creates a channel with the specified ID and basic format
     *
     * @param id             The ID of the channel
     * @param format         The channel format
     * @param broadcastScope The {@link BroadcastScope} of this channel
     */
    public Channel(@NotNull String id, @NotNull String format, @NotNull BroadcastScope broadcastScope) {
        this.id = id;
        this.format = format;
        this.broadcastScope = broadcastScope;
    }

    /**
     * The ID of the channel
     */
    @NotNull
    public String getId() {
        return id;
    }

    /**
     * Message format of the channel
     */
    @NotNull
    public String getFormat() {
        return format;
    }

    /**
     * {@link BroadcastScope} of the channel
     */
    @NotNull
    public BroadcastScope getBroadcastScope() {
        return broadcastScope;
    }


    /**
     * A {@link Set} of shortcut commands users can execute to access the channel
     */
    @NotNull
    public List<String> getShortcutCommands() {
        return shortcutCommands;
    }


    /**
     * A {@link ArrayList} of servers where this channel cannot be used
     */
    public void setShortcutCommands(List<String> shortcutCommands) {
        this.shortcutCommands = shortcutCommands;
    }


    /**
     * Permission node required to switch to and send messages to this channel
     */
    @NotNull
    public List<String> getRestrictedServers() {
        return restrictedServers;
    }

    /**
     * Permission node required to receive messages from this channel. Note that channels with a {@link BroadcastScope} of {@code PASSTHROUGH}, {@code LOCAL_PASSTHROUGH} or {@code GLOBAL_PASSTHROUGH} will pass messages to the backend server, which will not necessarily check for this node, meaning players without permission may receive channel messages.,
     */
    public void setRestrictedServers(List<String> restrictedServers) {
        this.restrictedServers = restrictedServers;
    }

    /**
     * Whether this channel should have its messages logged to console
     */
    @Nullable
    public String getSendPermission() {
        return sendPermission;
    }

    /**
     * String identifier of the channel
     */
    public void setSendPermission(@Nullable String sendPermission) {
        this.sendPermission = sendPermission;
    }

    /**
     * Whether this channel should automatically apply filters to messages
     */
    @Nullable
    public String getReceivePermission() {
        return receivePermission;
    }

    public void setReceivePermission(@Nullable String receivePermission) {
        this.receivePermission = receivePermission;
    }

    public boolean doLogMessages() {
        return logMessages;
    }

    public void setLogMessages(boolean logMessages) {
        this.logMessages = logMessages;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    /**
     * The broadcast scope of a channel, defining how messages will be handled
     */
    public enum BroadcastScope {
        /**
         * Message is broadcast globally to those with permissions via the proxy
         */
        GLOBAL(false),

        /**
         * Message is broadcast via the proxy to players who have permission and are on the same server as the source
         */
        LOCAL(false),

        /**
         * Message is not handled by the proxy and is instead passed to the backend server
         */
        PASSTHROUGH(true),

        /**
         * Message is broadcast via the proxy to players who have permission and are on the same server as the source and is additionally passed to the backend server
         */
        LOCAL_PASSTHROUGH(true),

        /**
         * Message is broadcast globally to those with permissions via the proxy and is additionally passed to the backend server
         */
        GLOBAL_PASSTHROUGH(true);

        /**
         * Whether the broadcast should be passed to the backend server
         */
        public final boolean isPassThrough;

        BroadcastScope(boolean isPassThrough) {
            this.isPassThrough = isPassThrough;
        }
    }
}
