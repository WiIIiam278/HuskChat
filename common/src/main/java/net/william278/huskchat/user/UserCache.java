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

package net.william278.huskchat.user;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * A cache for persisting player data
 */
public class UserCache {

    private final HuskChat plugin;
    private final Map<UUID, String> playerChannels;
    private final HashMap<UUID, SpyColor> localSpies = new HashMap<>();
    private final HashMap<UUID, SpyColor> socialSpies = new HashMap<>();

    public UserCache(@NotNull HuskChat plugin) {
        this.plugin = plugin;
        this.playerChannels = new LinkedHashMap<>();

        try {
            final YamlDocument spies = YamlDocument.create(new File(plugin.getDataFolder(), "spies.yml"));
            if (spies.contains("local")) {
                Section local = spies.getSection("local");

                for (Object name : local.getKeys()) {
                    localSpies.put(UUID.fromString(name.toString()),
                            SpyColor.valueOf(local.getSection(name.toString()).getString("color")));
                }
            }

            if (spies.contains("social")) {
                Section social = spies.getSection("social");

                for (Object name : social.getKeys()) {
                    socialSpies.put(UUID.fromString(name.toString()),
                            SpyColor.valueOf(social.getSection(name.toString()).getString("color")));
                }
            }
        } catch (IOException e) {
            plugin.log(Level.WARNING, "Error loading spy data", e);
        }
    }

    public String getPlayerChannel(UUID uuid) {
        if (!playerChannels.containsKey(uuid)) {
            return plugin.getSettings().getDefaultChannel();
        }
        return playerChannels.get(uuid);
    }

    public void setPlayerChannel(UUID uuid, String playerChannel) {
        playerChannels.put(uuid, playerChannel);
    }


    /**
     * Switch the {@link OnlineUser}'s channel
     *
     * @param player    {@link OnlineUser} to switch the channel of
     * @param channelID ID of the channel to switch to
     */
    public void switchPlayerChannel(@NotNull OnlineUser player, @NotNull String channelID) {
        final Channel channel = plugin.getSettings().getChannels().get(channelID);
        if (channel == null) {
            plugin.getLocales().sendMessage(player, "error_invalid_channel");
            return;
        }

        if (channel.getSendPermission() != null) {
            if (!player.hasPermission(channel.getSendPermission())) {
                plugin.getLocales().sendMessage(player, "error_no_permission_send", channel.getId());
                return;
            }
        }
        setPlayerChannel(player.getUuid(), channel.getId());
        plugin.getLocales().sendMessage(player, "channel_switched", channel.getId());
    }


    // Map of users last private message target for /reply command
    @NotNull
    private static final Map<UUID, Set<UUID>> lastMessagePlayers = new HashMap<>();

    public static Optional<Set<UUID>> getLastMessengers(@NotNull UUID uuid) {
        if (lastMessagePlayers.containsKey(uuid)) {
            return Optional.of(lastMessagePlayers.get(uuid));
        }
        return Optional.empty();
    }

    public static void setLastMessenger(@NotNull UUID playerToSet, @NotNull List<OnlineUser> lastMessengers) {
        final HashSet<UUID> uuidPlayers = new HashSet<>();
        for (OnlineUser player : lastMessengers) {
            uuidPlayers.add(player.getUuid());
        }
        lastMessagePlayers.put(playerToSet, uuidPlayers);
    }

    public boolean isSocialSpying(@NotNull OnlineUser player) {
        return socialSpies.containsKey(player.getUuid());
    }

    public void setSocialSpy(@NotNull OnlineUser player) throws IOException {
        socialSpies.put(player.getUuid(), SpyColor.DEFAULT_SPY_COLOR);
        addSpy("social", player.getUuid(), SpyColor.DEFAULT_SPY_COLOR);
    }

    public void setSocialSpy(@NotNull OnlineUser player, @NotNull SpyColor spyColor) throws IOException {
        socialSpies.put(player.getUuid(), spyColor);
        addSpy("social", player.getUuid(), spyColor);
    }

    public void removeSocialSpy(@NotNull OnlineUser player) throws IOException {
        socialSpies.remove(player.getUuid());
        removeSpy("social", player.getUuid());
    }

    // Determines who is going to receive a spy message
    @NotNull
    public Map<OnlineUser, SpyColor> getSocialSpyMessageReceivers(@NotNull List<OnlineUser> messageRecipients) {
        final Map<OnlineUser, SpyColor> receivers = new LinkedHashMap<>();

        calculateSpies:
        for (UUID player : socialSpies.keySet()) {
            final SpyColor color = socialSpies.get(player);
            final Optional<OnlineUser> spy = plugin.getPlayer(player);
            if (spy.isEmpty()) {
                continue;
            }
            for (OnlineUser messageRecipient : messageRecipients) {
                if (player.equals(messageRecipient.getUuid())) {
                    continue calculateSpies;
                }
            }
            receivers.put(spy.get(), color);
        }
        return receivers;
    }

    public boolean isLocalSpying(OnlineUser player) {
        return localSpies.containsKey(player.getUuid());
    }

    public void setLocalSpy(OnlineUser player) throws IOException {
        localSpies.put(player.getUuid(), SpyColor.DEFAULT_SPY_COLOR);
        addSpy("local", player.getUuid(), SpyColor.DEFAULT_SPY_COLOR);
    }

    public void setLocalSpy(OnlineUser player, SpyColor spyColor) throws IOException {
        localSpies.put(player.getUuid(), spyColor);
        addSpy("local", player.getUuid(), spyColor);
    }

    public void removeLocalSpy(OnlineUser player) throws IOException {
        localSpies.remove(player.getUuid());
        removeSpy("local", player.getUuid());
    }

    @NotNull
    public Map<OnlineUser, SpyColor> getLocalSpyMessageReceivers(String localMessageServer, HuskChat implementor) {
        final Map<OnlineUser, SpyColor> receivers = new LinkedHashMap<>();
        for (UUID player : localSpies.keySet()) {
            final SpyColor color = localSpies.get(player);
            final Optional<OnlineUser> spy = implementor.getPlayer(player);
            if (spy.isEmpty()) {
                continue;
            }
            if (spy.get().getServerName().equals(localMessageServer)) {
                continue;
            }
            receivers.put(spy.get(), color);
        }
        return receivers;
    }

    // Adds spy state to data file
    public void addSpy(String type, UUID uuid, SpyColor spyColor) throws IOException {
        if (!type.equals("local") && !type.equals("social")) {
            return;
        }
        final YamlDocument spies = YamlDocument.create(new File(plugin.getDataFolder(), "spies.yml"));

        if (!spies.contains(type)) {
            spies.createSection(type);
        }
        if (!spies.getSection(type).contains(uuid.toString())) {
            spies.getSection(type).createSection(uuid.toString());
        }

        spies.getSection(type)
                .getSection(uuid.toString())
                .set("color", spyColor.toString());
        spies.save();
    }

    // Removes spy state from data file
    public void removeSpy(String type, UUID uuid) throws IOException {
        if (!type.equals("local") && !type.equals("social")) return;

        YamlDocument spies = YamlDocument.create(new File(plugin.getDataFolder(), "spies.yml"));

        if (!spies.contains(type)) return;
        if (!spies.getSection(type).contains(uuid.toString())) return;

        spies.getSection(type)
                .remove(uuid.toString());

        if (spies.getSection(type).getKeys().size() == 0) {
            spies.remove(type);
        }

        spies.save();
    }


    /**
     * Color used for displaying chat
     */
    public enum SpyColor {
        DARK_RED("&4"),
        RED("&c"),
        GOLD("&6"),
        YELLOW("&e"),
        DARK_GREEN("&2"),
        GREEN("&a"),
        AQUA("&b"),
        DARK_AQUA("&3"),
        DARK_BLUE("&1"),
        BLUE("&9"),
        LIGHT_PURPLE("&d"),
        DARK_PURPLE("&5"),
        WHITE("&f"),
        GRAY("&7"),
        DARK_GRAY("&8"),
        BLACK("&9");

        public static final SpyColor DEFAULT_SPY_COLOR = DARK_GRAY;
        public final String colorCode;

        SpyColor(@NotNull String colorCode) {
            this.colorCode = colorCode;
        }

        @NotNull
        public static List<String> getColorStrings() {
            List<String> colors = new ArrayList<>();
            for (SpyColor color : SpyColor.values()) {
                colors.add(color.name().toLowerCase());
            }
            return colors;
        }

        public static Optional<SpyColor> getColor(@NotNull String colorInput) {
            for (SpyColor color : SpyColor.values()) {
                if (color.colorCode.replace("&", "").equals(colorInput.replace("&", ""))
                    || color.name().equalsIgnoreCase(colorInput.toUpperCase())) {
                    return Optional.of(color);
                }
            }
            return Optional.empty();
        }
    }

}
