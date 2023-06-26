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
import net.william278.huskchat.filter.*;
import net.william278.huskchat.replacer.EmojiReplacer;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Class for loading and storing plugin settings and {@link Channel channels}
 */
public class Settings {

    // Plugin language
    private String language;


    // Channel config
    private String defaultChannel;
    private HashMap<String, String> serverDefaultChannels;
    private HashMap<String, Channel> channels;
    private String channelLogFormat;
    private List<String> channelCommandAliases;


    // Message command config
    private boolean doMessageCommand;
    private boolean doGroupMessages;
    private int maxGroupMessageSize;
    private List<String> messageCommandAliases;
    private List<String> replyCommandAliases;
    private String inboundMessageFormat;
    private String outboundMessageFormat;
    private String groupInboundMessageFormat;
    private String groupOutboundMessageFormat;
    private boolean logPrivateMessages;
    private boolean censorPrivateMessages;
    private String messageLogFormat;
    private List<String> messageCommandRestrictedServers;

    // Social spy
    private boolean doSocialSpyCommand;
    private String socialSpyFormat;
    private String socialSpyGroupFormat;
    private List<String> socialSpyCommandAliases;


    // Local spy
    private boolean doLocalSpyCommand;
    private String localSpyFormat;
    private List<String> excludedLocalSpyChannels;
    private List<String> localSpyCommandAliases;


    // Broadcast command
    private boolean doBroadcastCommand;
    private List<String> broadcastCommandAliases;
    private String broadcastMessageFormat;
    private boolean logBroadcasts;
    private String broadcastLogFormat;


    // Chat filters
    private Map<String, List<ChatFilter>> chatFilters;


    // Discord integration
    private boolean doDiscordIntegration;
    private Map<String, URL> webhookUrls;
    private Webhook.Format webhookFormat;

    // Server names
    private Map<String, String> serverNameReplacement;


    public Settings(@NotNull YamlDocument configFile) {
        this.loadConfig(configFile);
    }

    private void loadConfig(@NotNull YamlDocument configFile) {
        // Language file
        language = configFile.getString("language", "en-gb");

        // Channels
        defaultChannel = configFile.getString("default_channel", "global");
        channelLogFormat = configFile.getString("channel_log_format", "[CHAT] [%channel%] %sender%: ");
        channels = new LinkedHashMap<>();
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
        replyCommandAliases = new ArrayList<>();
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
        webhookFormat = Webhook.Format.getMessageFormat(configFile.getString("discord.format_style", "inline"))
                .orElse(Webhook.Format.INLINE);
        webhookUrls = fetchWebhookUrls(configFile);

        // Server name replacement
        serverNameReplacement = new LinkedHashMap<>();
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
    private HashMap<String, Channel> fetchChannels(YamlDocument configFile) throws IllegalArgumentException {
        final HashMap<String, Channel> channels = new HashMap<>();
        for (String channelID : configFile.getSection("channels").getRoutesAsStrings(false)) {
            // Get channel format and scope and create channel object
            final String format = configFile.getString("channels." + channelID + ".format", "%fullname%&r: ");
            final String broadcastScope = configFile.getString("channels." + channelID + ".broadcast_scope", "GLOBAL").toUpperCase();
            Channel channel = new Channel(channelID.toLowerCase(), format, Channel.BroadcastScope.valueOf(broadcastScope));

            // Read shortcut commands
            if (configFile.contains("channels." + channelID + ".shortcut_commands")) {
                channel.setShortcutCommands(getCommandsFromList(configFile.getStringList("channels." + channelID + ".shortcut_commands")));
            }

            // Read shortcut commands
            if (configFile.contains("channels." + channelID + ".restricted_servers")) {
                channel.setRestrictedServers(configFile.getStringList("channels." + channelID + ".restricted_servers"));
            }

            // Read optional parameters
            channel.setSendPermission(configFile.getString("channels." + channelID + ".permissions.send", null));
            channel.setReceivePermission(configFile.getString("channels." + channelID + ".permissions.receive", null));
            channel.setLogMessages(configFile.getBoolean("channels." + channelID + ".log_to_console", true));
            channel.setFilter(configFile.getBoolean("channels." + channelID + ".filtered", true));

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
    private Map<String, List<ChatFilter>> fetchChatFilters(YamlDocument configFile) {
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
    private Map<String, URL> fetchWebhookUrls(@NotNull YamlDocument configFile) {
        final Map<String, URL> webhookUrls = new HashMap<>();
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
    private HashMap<String, String> getServerDefaultChannels(YamlDocument configFile) {
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
    private List<String> getCommandsFromList(List<String> rawCommands) {
        List<String> commands = new ArrayList<>();
        for (String command : rawCommands) {
            commands.add(command.substring(1));
        }
        return commands;
    }

    /**
     * Clears the chat filters and disposes of the existing ProfanityFilter
     */
    private void clearChatFilters() {
        chatFilters = new HashMap<>();
    }

    /**
     * Returns the aliases from a list of aliases
     *
     * @param aliases The alias list
     * @return The actual command aliases
     */
    public String[] getAliases(List<String> aliases) {
        if (aliases.size() <= 1) {
            return new String[0];
        }
        String[] aliasList = new String[aliases.size() - 1];
        for (int i = 1; i < aliases.size(); i++) {
            aliasList[i - 1] = aliases.get(i);
        }
        return aliasList;
    }

    @NotNull
    public String getLanguage() {
        return language;
    }

    @NotNull
    public String getDefaultChannel() {
        return defaultChannel;
    }

    @NotNull
    public Map<String, String> getServerDefaultChannels() {
        return serverDefaultChannels;
    }

    @NotNull
    public Map<String, Channel> getChannels() {
        return channels;
    }

    @NotNull
    public String getChannelLogFormat() {
        return channelLogFormat;
    }

    @NotNull
    public List<String> getChannelCommandAliases() {
        return channelCommandAliases;
    }

    public boolean isDoMessageCommand() {
        return doMessageCommand;
    }

    public boolean doGroupMessages() {
        return doGroupMessages;
    }

    public int getMaxGroupMessageSize() {
        return maxGroupMessageSize;
    }

    @NotNull
    public List<String> getMessageCommandAliases() {
        return messageCommandAliases;
    }

    @NotNull
    public List<String> getReplyCommandAliases() {
        return replyCommandAliases;
    }

    @NotNull
    public String getInboundMessageFormat() {
        return inboundMessageFormat;
    }

    @NotNull
    public String getOutboundMessageFormat() {
        return outboundMessageFormat;
    }

    @NotNull
    public String getGroupInboundMessageFormat() {
        return groupInboundMessageFormat;
    }

    @NotNull
    public String getGroupOutboundMessageFormat() {
        return groupOutboundMessageFormat;
    }

    public boolean doLogPrivateMessages() {
        return logPrivateMessages;
    }

    public boolean isCensorPrivateMessages() {
        return censorPrivateMessages;
    }

    @NotNull
    public String getMessageLogFormat() {
        return messageLogFormat;
    }

    @NotNull
    public List<String> getMessageCommandRestrictedServers() {
        return messageCommandRestrictedServers;
    }

    public boolean doSocialSpyCommand() {
        return doSocialSpyCommand;
    }

    @NotNull
    public String getSocialSpyFormat() {
        return socialSpyFormat;
    }

    @NotNull
    public String getSocialSpyGroupFormat() {
        return socialSpyGroupFormat;
    }

    @NotNull
    public List<String> getSocialSpyCommandAliases() {
        return socialSpyCommandAliases;
    }

    public boolean doLocalSpyCommand() {
        return doLocalSpyCommand;
    }

    @NotNull
    public String getLocalSpyFormat() {
        return localSpyFormat;
    }

    /**
     * Returns if a channel is excluded from local spy messages
     *
     * @param channel {@link Channel} to check
     * @return {@code true} if the channel should be excluded from /localspy; {@code false} otherwise
     */
    public boolean isLocalSpyChannelExcluded(@NotNull Channel channel) {
        for (String excludedChannel : excludedLocalSpyChannels) {
            if (excludedChannel.equals(channel.getId())) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    public List<String> getLocalSpyCommandAliases() {
        return localSpyCommandAliases;
    }

    public boolean isDoBroadcastCommand() {
        return doBroadcastCommand;
    }

    @NotNull
    public List<String> getBroadcastCommandAliases() {
        return broadcastCommandAliases;
    }

    @NotNull
    public String getBroadcastMessageFormat() {
        return broadcastMessageFormat;
    }

    public boolean doLogBroadcasts() {
        return logBroadcasts;
    }

    @NotNull
    public String getBroadcastLogFormat() {
        return broadcastLogFormat;
    }

    @NotNull
    public Map<String, List<ChatFilter>> getChatFilters() {
        return chatFilters;
    }

    public boolean doDiscordIntegration() {
        return doDiscordIntegration;
    }

    public Map<String, URL> getWebhookUrls() {
        return webhookUrls;
    }

    public Webhook.Format getWebhookMessageFormat() {
        return webhookFormat;
    }

    public Map<String, String> getServerNameReplacement() {
        return serverNameReplacement;
    }
}
