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

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.discord.DiscordMessageFormat;
import net.william278.huskchat.filter.*;
import net.william278.huskchat.filter.replacer.EmojiReplacer;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Stores plugin settings and {@link Channel} data
 */
public class Settings {

    // Plugin language
    public static String language;

    // Channel config
    public static String defaultChannel;
    public static HashMap<String, String> serverDefaultChannels = new HashMap<>();
    public static HashMap<String, Channel> channels = new HashMap<>();
    public static String channelLogFormat;
    public static List<String> channelCommandAliases = new ArrayList<>();

    // Message command config
    public static boolean doMessageCommand;

    public static boolean doGroupMessages;

    public static int maxGroupMessageSize;
    public static List<String> messageCommandAliases = new ArrayList<>();
    public static List<String> replyCommandAliases = new ArrayList<>();
    public static String inboundMessageFormat;
    public static String outboundMessageFormat;
    public static String groupInboundMessageFormat;
    public static String groupOutboundMessageFormat;
    public static boolean logPrivateMessages;
    public static boolean censorPrivateMessages;
    public static String messageLogFormat;
    public static List<String> messageCommandRestrictedServers = new ArrayList<>();

    // Social spy
    public static boolean doSocialSpyCommand;
    public static String socialSpyFormat;
    public static String socialSpyGroupFormat;
    public static List<String> socialSpyCommandAliases = new ArrayList<>();

    // Local spy
    public static boolean doLocalSpyCommand;
    public static String localSpyFormat;
    public static List<String> excludedLocalSpyChannels = new ArrayList<>();
    public static List<String> localSpyCommandAliases = new ArrayList<>();

    // Broadcast command
    public static boolean doBroadcastCommand;
    public static List<String> broadcastCommandAliases = new ArrayList<>();
    public static String broadcastMessageFormat;
    public static boolean logBroadcasts;
    public static String broadcastLogFormat;

    // Chat filters
    public static Map<String, List<ChatFilter>> chatFilters = new HashMap<>();

    // Discord integration
    public static boolean doDiscordIntegration;
    public static Map<String, URL> webhookUrls = new HashMap<>();
    public static DiscordMessageFormat webhookMessageFormat;

    public static Map<String, String> serverNameReplacement = new HashMap<>();

    /**
     * Use {@link Settings#load(YamlDocument)}
     */
    private Settings() {
    }

    /**
     * Load plugin Settings from a config file
     *
     * @param configFile Proxy {@link YamlDocument} data
     */
    public static void load(YamlDocument configFile) {
        // Language file
        language = configFile.getString("language", "en-gb");

        // Channels
        defaultChannel = configFile.getString("default_channel", "global");
        channelLogFormat = configFile.getString("channel_log_format", "[CHAT] [%channel%] %sender%: ");
        channels.putAll(fetchChannels(configFile));
        serverDefaultChannels = getServerDefaultChannels(configFile);
        channelCommandAliases = (configFile.contains("channel_command_aliases")) ?
                getCommandsFromList(configFile.getStringList("channel_command_aliases")) :
                Collections.singletonList("channel");

        // Message command options
        doMessageCommand = configFile.getBoolean("message_command.enabled", true);
        doGroupMessages = configFile.getBoolean("message_command.group_messages.enabled", true);
        maxGroupMessageSize = configFile.getInt("message_command.group_messages.max_size", 5);
        inboundMessageFormat = configFile.getString("message_command.format.inbound",
                "&#00fb9a&%name% &8→ &#00fb9a&You&8: &f");
        outboundMessageFormat = configFile.getString("message_command.format.outbound",
                "&#00fb9a&You &8→ &#00fb9a&%name%&8 &f");
        groupInboundMessageFormat = configFile.getString("message_command.format.group_inbound",
                "&#00fb9a&%name% &8→ &#00fb9a&You[₍₊%group_amount_subscript%₎](gray show_text=&7%group_members%)&8: &f");
        groupOutboundMessageFormat = configFile.getString("message_command.format.group_outbound",
                "&#00fb9a&You &8→ &#00fb9a&%name%[₍₊%group_amount_subscript%₎](gray show_text=&7%group_members%)&8: &f");
        logPrivateMessages = configFile.getBoolean("message_command.log_to_console", true);
        censorPrivateMessages = configFile.getBoolean("message_command.censor", false);
        messageLogFormat = configFile.getString("message_command.log_format", "[MSG] [%sender% -> %receiver%]: ");
        messageCommandRestrictedServers = configFile.getStringList("message_command.restricted_servers");
        messageCommandAliases = (configFile.contains("message_command.msg_aliases")) ?
                getCommandsFromList(configFile.getStringList("message_command.msg_aliases")) :
                Collections.singletonList("msg");
        replyCommandAliases = (configFile.contains("message_command.reply_aliases")) ?
                getCommandsFromList(configFile.getStringList("message_command.reply_aliases")) :
                Collections.singletonList("reply");

        // Social spy
        doSocialSpyCommand = configFile.getBoolean("social_spy.enabled", true);
        socialSpyFormat = configFile.getString("social_spy.format", "&e[Spy] &7%sender% &8→ &7%receiver%:%spy_color% ");
        socialSpyGroupFormat = configFile.getString("social_spy.group_format", "&e[Spy] &7%sender_name% &8→ &7%receiver_name%[₍₊%group_amount_subscript%₎](gray show_text=&7%group_members%):%spy_color% ");
        socialSpyCommandAliases = (configFile.contains("social_spy.socialspy_aliases")) ?
                getCommandsFromList(configFile.getStringList("social_spy.socialspy_aliases")) :
                Collections.singletonList("socialspy");

        // Local spy
        doLocalSpyCommand = configFile.getBoolean("local_spy.enabled", true);
        localSpyFormat = configFile.getString("local_spy.format", "&e[Spy] &7[%channel%] %name%&8:%spy_color% ");
        excludedLocalSpyChannels = (configFile.contains("local_spy.excluded_local_channels")) ? configFile.getStringList("local_spy.excluded_local_channels") : new ArrayList<>();
        localSpyCommandAliases = (configFile.contains("local_spy.localspy_aliases")) ?
                getCommandsFromList(configFile.getStringList("local_spy.localspy_aliases")) :
                Collections.singletonList("localspy");

        // Broadcast command
        doBroadcastCommand = configFile.getBoolean("broadcast_command.enabled", true);
        broadcastCommandAliases = (configFile.contains("broadcast_command.broadcast_aliases")) ?
                getCommandsFromList(configFile.getStringList("broadcast_command.broadcast_aliases")) :
                Collections.singletonList("broadcast");
        broadcastMessageFormat = configFile.getString("broadcast_command.format", "&6[Broadcast]&e ");
        logBroadcasts = configFile.getBoolean("broadcast_command.log_to_console", true);
        broadcastLogFormat = configFile.getString("broadcast_command.log_format", "[BROADCAST]: ");

        // Chat filters
        chatFilters = fetchChatFilters(configFile);

        // Discord integration
        doDiscordIntegration = configFile.getBoolean("discord.enabled", false);
        webhookMessageFormat = DiscordMessageFormat.getMessageFormat(configFile.getString("discord.format_style", "inline"))
                .orElse(DiscordMessageFormat.INLINE);
        webhookUrls = fetchWebhookUrls(configFile);

        // Server name replacement
        Section serverNameReplacementSection = configFile.getSection("server_name_replacement");
        if (serverNameReplacementSection != null) {
            for (String s : serverNameReplacementSection.getRoutesAsStrings(false)) {
                serverNameReplacement.put(s, serverNameReplacementSection.getString(s));
            }
        }
    }

    /**
     * Returns {@link Channel} data from the proxy {@link YamlDocument}
     *
     * @param configFile The proxy {@link YamlDocument}
     * @return {@link HashMap} of {@link Channel} data listed in the config file
     * @throws IllegalArgumentException if a channel contains an invalid broadcast scope
     */
    private static HashMap<String, Channel> fetchChannels(YamlDocument configFile) throws IllegalArgumentException {
        final HashMap<String, Channel> channels = new HashMap<>();
        for (String channelID : configFile.getSection("channels").getRoutesAsStrings(false)) {
            // Get channel format and scope and create channel object
            final String format = configFile.getString("channels." + channelID + ".format", "%fullname%&r: ");
            final String broadcastScope = configFile.getString("channels." + channelID + ".broadcast_scope", "GLOBAL").toUpperCase();
            Channel channel = new Channel(channelID.toLowerCase(), format, Channel.BroadcastScope.valueOf(broadcastScope));

            // Read shortcut commands
            if (configFile.contains("channels." + channelID + ".shortcut_commands")) {
                channel.shortcutCommands = getCommandsFromList(configFile.getStringList("channels." + channelID + ".shortcut_commands"));
            }

            // Read shortcut commands
            if (configFile.contains("channels." + channelID + ".restricted_servers")) {
                channel.restrictedServers = configFile.getStringList("channels." + channelID + ".restricted_servers");
            }

            // Read optional parameters
            channel.sendPermission = configFile.getString("channels." + channelID + ".permissions.send", null);
            channel.receivePermission = configFile.getString("channels." + channelID + ".permissions.receive", null);
            channel.logMessages = configFile.getBoolean("channels." + channelID + ".log_to_console", true);
            channel.filter = configFile.getBoolean("channels." + channelID + ".filtered", true);

            channels.put(channelID, channel);
        }
        return channels;
    }

    /**
     * Returns a {@link Set} of {@link ChatFilter}s to use for filtering chat messages
     *
     * @param configFile The proxy {@link YamlDocument}
     * @return {@link ChatFilter}s to use
     */
    private static Map<String, List<ChatFilter>> fetchChatFilters(YamlDocument configFile) {
        Map<String, List<ChatFilter>> filters = new HashMap<>();
        clearChatFilters(); // Clear and dispose of any existing ProfanityChecker instances

        for (String channelID : configFile.getSection("channels").getRoutesAsStrings(false)) {
            filters.put(channelID, new ArrayList<>());
        }

        filters.put("private_messages", new ArrayList<>());
        filters.put("broadcast_messages", new ArrayList<>());

        // Filters
        if (configFile.getBoolean("chat_filters.advertising_filter.enabled", true)) {
            List<String> channels = configFile.getStringList("chat_filters.advertising_filter.channels");

            if (configFile.getBoolean("chat_filters.advertising_filter.private_messages", false)) {
                channels.add("private_messages");
            }

            if (configFile.getBoolean("chat_filters.advertising_filter.broadcast_messages", false)) {
                channels.add("broadcast_messages");
            }

            for (String channel : channels) {
                if (!filters.containsKey(channel)) continue;
                filters.get(channel).add(new AdvertisingFilterer());
            }
        }
        if (configFile.getBoolean("chat_filters.caps_filter.enabled", true)) {
            List<String> channels = configFile.getStringList("chat_filters.caps_filter.channels");

            if (configFile.getBoolean("chat_filters.caps_filter.private_messages", false)) {
                channels.add("private_messages");
            }

            if (configFile.getBoolean("chat_filters.caps_filter.broadcast_messages", false)) {
                channels.add("broadcast_messages");
            }

            for (String channel : channels) {
                if (!filters.containsKey(channel)) continue;
                filters.get(channel).add(new CapsFilter(configFile.getDouble("chat_filters.caps_filter.max_caps_percentage", 0.4)));
            }
        }
        if (configFile.getBoolean("chat_filters.spam_filter.enabled", true)) {
            List<String> channels = configFile.getStringList("chat_filters.spam_filter.channels");

            if (configFile.getBoolean("chat_filters.spam_filter.private_messages", false)) {
                channels.add("private_messages");
            }

            if (configFile.getBoolean("chat_filters.spam_filter.broadcast_messages", false)) {
                channels.add("broadcast_messages");
            }

            for (String channel : channels) {
                if (!filters.containsKey(channel)) continue;
                filters.get(channel).add(new SpamFilter(configFile.getInt("chat_filters.spam_filter.period_seconds", 4),
                        configFile.getInt("chat_filters.spam_filter.messages_per_period", 3)));
            }
        }
        if (configFile.getBoolean("chat_filters.repeat_filter.enabled", true)) {
            List<String> channels = configFile.getStringList("chat_filters.repeat_filter.channels");

            if (configFile.getBoolean("chat_filters.repeat_filter.private_messages", false)) {
                channels.add("private_messages");
            }

            if (configFile.getBoolean("chat_filters.repeat_filter.broadcast_messages", false)) {
                channels.add("broadcast_messages");
            }

            for (String channel : channels) {
                if (!filters.containsKey(channel)) continue;
                filters.get(channel).add(new RepeatFilter(configFile.getInt("chat_filters.repeat_filter.previous_messages_to_check", 2)));
            }
        }
        if (configFile.getBoolean("chat_filters.profanity_filter.enabled", false)) {
            List<String> channels = configFile.getStringList("chat_filters.profanity_filter.channels");

            if (configFile.getBoolean("chat_filters.profanity_filter.private_messages", false)) {
                channels.add("private_messages");
            }

            if (configFile.getBoolean("chat_filters.profanity_filter.broadcast_messages", false)) {
                channels.add("broadcast_messages");
            }

            for (String channel : channels) {
                if (!filters.containsKey(channel)) continue;
                filters.get(channel).add(new ProfanityFilterer(ProfanityFilterer.ProfanityFilterMode.valueOf(
                        configFile.getString("chat_filters.profanity_filter.mode", "TOLERANCE").toUpperCase()),
                        configFile.getDouble("chat_filters.profanity_filter.tolerance", 0.78d),
                        configFile.getString("chat_filters.profanity_filter.library_path", "")));
            }
        }
        if (configFile.getBoolean("chat_filters.ascii_filter.enabled", false)) {
            List<String> channels = configFile.getStringList("chat_filters.ascii_filter.channels");

            if (configFile.getBoolean("chat_filters.ascii_filter.private_messages", false)) {
                channels.add("private_messages");
            }

            if (configFile.getBoolean("chat_filters.ascii_filter.broadcast_messages", false)) {
                channels.add("broadcast_messages");
            }

            for (String channel : channels) {
                if (!filters.containsKey(channel)) continue;
                filters.get(channel).add(new AsciiFilter());
            }
        }

        // Replacers
        if (configFile.getBoolean("message_replacers.emoji_replacer.enabled", true)) {
            HashMap<String, String> emojiSequences = new HashMap<>();
            boolean caseInsensitive = configFile.getBoolean("message_replacers.emoji_replacer.case_insensitive", false);
            for (String characters : configFile.getSection("message_replacers.emoji_replacer.emoji").getRoutesAsStrings(false)) {
                if (!caseInsensitive) {
                    emojiSequences.put(characters, configFile.getString("message_replacers.emoji_replacer.emoji." + characters));
                } else {
                    emojiSequences.put(characters.toLowerCase(Locale.ROOT), configFile.getString("message_replacers.emoji_replacer.emoji." + characters));
                }
            }

            List<String> channels = configFile.getStringList("message_replacers.emoji_replacer.channels");

            if (configFile.getBoolean("chat_filters.emoji_replacer.private_messages", true)) {
                channels.add("private_messages");
            }

            if (configFile.getBoolean("chat_filters.emoji_replacer.broadcast_messages", true)) {
                channels.add("broadcast_messages");
            }

            for (String channel : channels) {
                if (!filters.containsKey(channel)) continue;
                filters.get(channel).add(new EmojiReplacer(emojiSequences, caseInsensitive));
            }
        }

        return filters;
    }

    /**
     * Returns a map of Discord webhook URLs for each channel
     *
     * @param configFile The configuration file
     * @return A map of webhook URLs for each channel
     */
    private static HashMap<String, URL> fetchWebhookUrls(@NotNull YamlDocument configFile) {
        HashMap<String, URL> webhookUrls = new HashMap<>();
        try {
            if (configFile.contains("discord.channel_webhooks")) {
                for (String channelID : configFile.getSection("discord.channel_webhooks").getRoutesAsStrings(false)) {
                    if (!channels.containsKey(channelID)) {
                        continue;
                    }
                    webhookUrls.put(channelID, new URL(configFile.getString("discord.channel_webhooks." + channelID)));
                }
            }
        } catch (MalformedURLException e) {
            doDiscordIntegration = false;
        }
        return webhookUrls;
    }

    /**
     * Returns a {@link java.util.Map} of servers to the default channel to enforce on that server
     *
     * @param configFile The proxy {@link YamlDocument}
     * @return {@link java.util.Map} of servers and their default channels
     */
    private static HashMap<String, String> getServerDefaultChannels(YamlDocument configFile) {
        final HashMap<String, String> serverDefaults = new HashMap<>();
        if (configFile.contains("server_default_channels")) {
            for (String server : configFile.getSection("server_default_channels").getRoutesAsStrings(false)) {
                String channelId = configFile.getString("server_default_channels." + server);
                serverDefaults.put(server, channelId);
            }
        }
        return serverDefaults;
    }

    /**
     * Returns a {@link Set} of formatted command strings from a list
     *
     * @param rawCommands Raw commands prepended with {@code /}
     * @return formatted set of command strings
     */
    private static List<String> getCommandsFromList(List<String> rawCommands) {
        List<String> commands = new ArrayList<>();
        for (String command : rawCommands) {
            commands.add(command.substring(1));
        }
        return commands;
    }

    /**
     * Returns if a channel is excluded from local spy messages
     *
     * @param channel {@link Channel} to check
     * @return {@code true} if the channel should be excluded from /localspy; {@code false} otherwise
     */
    public static boolean isLocalSpyChannelExcluded(Channel channel) {
        for (String excludedChannel : excludedLocalSpyChannels) {
            if (excludedChannel.equals(channel.id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clears the chat filters and disposes of the existing ProfanityFilter
     */
    private static void clearChatFilters() {
        chatFilters = new HashMap<>();
    }

    /**
     * Returns the aliases from a list of aliases
     *
     * @param aliases The alias list
     * @return The actual command aliases
     */
    public static String[] getAliases(List<String> aliases) {
        if (aliases.size() <= 1) {
            return new String[0];
        }
        String[] aliasList = new String[aliases.size() - 1];
        for (int i = 1; i < aliases.size(); i++) {
            aliasList[i - 1] = aliases.get(i);
        }
        return aliasList;
    }
}
