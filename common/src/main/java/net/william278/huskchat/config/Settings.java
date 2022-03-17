package net.william278.huskchat.config;

import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.filter.*;
import net.william278.huskchat.filter.replacer.EmojiReplacer;

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
    public static HashSet<Channel> channels = new HashSet<>();
    public static String channelLogFormat;
    public static List<String> channelCommandAliases = new ArrayList<>();

    // Message command config
    public static boolean doMessageCommand;
    public static List<String> messageCommandAliases = new ArrayList<>();
    public static List<String> replyCommandAliases = new ArrayList<>();
    public static String inboundMessageFormat;
    public static String outboundMessageFormat;
    public static boolean logPrivateMessages;
    public static boolean censorPrivateMessages;
    public static String messageLogFormat;
    public static List<String> messageCommandRestrictedServers = new ArrayList<>();

    // Social spy
    public static boolean doSocialSpyCommand;
    public static String socialSpyFormat;
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
    public static List<ChatFilter> chatFilters = new ArrayList<>();

    /**
     * Use {@link Settings#load(ConfigFile)}
     */
    private Settings() {
    }

    /**
     * Load plugin Settings from a config file
     *
     * @param configFile Proxy {@link ConfigFile} data
     */
    public static void load(ConfigFile configFile) {
        // Language file
        language = configFile.getString("language", "en-gb");

        // Channels
        defaultChannel = configFile.getString("default_channel", "global");
        channelLogFormat = configFile.getString("channel_log_format", "[CHAT] [%channel%] %sender%: ");
        channels.addAll(fetchChannels(configFile));
        serverDefaultChannels = getServerDefaultChannels(configFile);
        channelCommandAliases = (configFile.contains("channel_command_aliases")) ? getCommandsFromList(new ArrayList<>(configFile.getStringList("channel_command_aliases"))) : Collections.singletonList("channel");

        // Other options
        doMessageCommand = configFile.getBoolean("message_command.enabled", true);
        inboundMessageFormat = configFile.getString("message_command.format.inbound", "&#00fb9a&%name% &8→ &#00fb9a&You&8: &f");
        outboundMessageFormat = configFile.getString("message_command.format.outbound", "&#00fb9a&You &8→ &#00fb9a&%name%&8 &f");
        logPrivateMessages = configFile.getBoolean("messages_command.log_to_console", true);
        censorPrivateMessages = configFile.getBoolean("messages_command.censor", false);
        messageLogFormat = configFile.getString("messages_command.log_format", "[MSG] [%sender% -> %receiver%]: ");
        messageCommandRestrictedServers = configFile.getStringList("channels.messages_command.restricted_servers");
        messageCommandAliases = (configFile.contains("messages_command.msg_aliases")) ? getCommandsFromList(new ArrayList<>(configFile.getStringList("messages_command.msg_aliases"))) : Collections.singletonList("msg");
        replyCommandAliases = (configFile.contains("messages_command.reply_aliases")) ? getCommandsFromList(new ArrayList<>(configFile.getStringList("messages_command.reply_aliases"))) : Collections.singletonList("reply");

        // Social spy
        doSocialSpyCommand = configFile.getBoolean("social_spy.enabled", true);
        socialSpyFormat = configFile.getString("social_spy.format", "&e[Spy] &7%sender% &8→ &7%receiever%:%spy_color% ");
        socialSpyCommandAliases = (configFile.contains("social_spy.socialspy_aliases")) ? getCommandsFromList(new ArrayList<>(configFile.getStringList("social_spy.socialspy_aliases"))) : Collections.singletonList("socialspy");

        // Local spy
        doLocalSpyCommand = configFile.getBoolean("local_spy.enabled", true);
        localSpyFormat = configFile.getString("local_spy.format", "&e[Spy] &7[%channel%] %name%&8:%spy_color% ");
        excludedLocalSpyChannels = (configFile.contains("local_spy.excluded_local_channels")) ? configFile.getStringList("local_spy.excluded_local_channels") : new ArrayList<>();
        localSpyCommandAliases = (configFile.contains("local_spy.localspy_aliases")) ? getCommandsFromList(new ArrayList<>(configFile.getStringList("local_spy.localspy_aliases"))) : Collections.singletonList("localspy");

        // Broadcast command
        doBroadcastCommand = configFile.getBoolean("broadcast_command.enabled", true);
        broadcastCommandAliases = (configFile.contains("broadcast_command.broadcast_aliases")) ? getCommandsFromList(new ArrayList<>(configFile.getStringList("broadcast_command.broadcast_aliases"))) : Collections.singletonList("broadcast");
        broadcastMessageFormat = configFile.getString("broadcast_command.format", "&6[Broadcast]&e ");
        logBroadcasts = configFile.getBoolean("broadcast_command.log_to_console", true);
        broadcastLogFormat = configFile.getString("broadcast_command.log_format", "[BROADCAST]: ");

        // Chat filters
        chatFilters = fetchChatFilters(configFile);
    }

    /**
     * Returns {@link Channel} data from the proxy {@link ConfigFile}
     *
     * @param configFile The proxy {@link ConfigFile}
     * @return {@link HashSet} of {@link Channel} data listed in the config file
     * @throws IllegalArgumentException if a channel contains an invalid broadcast scope
     */
    private static HashSet<Channel> fetchChannels(ConfigFile configFile) throws IllegalArgumentException {
        final HashSet<Channel> channels = new HashSet<>();
        for (String channelID : configFile.getConfigKeys("channels")) {

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

            channels.add(channel);
        }
        return channels;
    }

    /**
     * Returns a {@link Set} of {@link ChatFilter}s to use for filtering chat messages
     *
     * @param configFile The proxy {@link ConfigFile}
     * @return {@link ChatFilter}s to use
     */
    private static List<ChatFilter> fetchChatFilters(ConfigFile configFile) {
        ArrayList<ChatFilter> filters = new ArrayList<>();
        clearChatFilters(); // Clear and dispose of any existing ProfanityChecker instances

        // Filters
        if (configFile.getBoolean("chat_filters.advertising_filter.enabled", true)) {
            filters.add(new AdvertisingFilterer());
        }
        if (configFile.getBoolean("chat_filters.caps_filter.enabled", true)) {
            filters.add(new CapsFilter(configFile.getDouble("chat_filters.caps_filter.max_caps_percentage", 0.4)));
        }
        if (configFile.getBoolean("chat_filters.spam_filter.enabled", true)) {
            filters.add(new SpamFilter(configFile.getInteger("chat_filters.spam_filter.period_seconds", 4),
                    configFile.getInteger("chat_filters.spam_filter.messages_per_period", 3)));
        }
        if (configFile.getBoolean("chat_filters.repeat_filter.enabled", true)) {
            filters.add(new RepeatFilter(configFile.getInteger("chat_filters.repeat_filter.previous_messages_to_check", 2)));
        }
        if (configFile.getBoolean("chat_filters.profanity_filter.enabled", false)) {
            filters.add(new ProfanityFilterer(ProfanityFilterer.ProfanityFilterMode.valueOf(
                    configFile.getString("chat_filters.profanity_filter.mode", "TOLERANCE").toUpperCase()),
                    configFile.getDouble("chat_filters.profanity_filter.tolerance", 0.78d),
                    configFile.getString("chat_filters.profanity_filter.library_path", "")));
        }
        if (configFile.getBoolean("chat_filters.ascii_filter.enabled", false)) {
            filters.add(new AsciiFilter());
        }

        // Replacers
        if (configFile.getBoolean("message_replacers.emoji_replacer.enabled", true)) {
            HashMap<String, String> emojiSequences = new HashMap<>();
            for (String characters : configFile.getConfigKeys("message_replacers.emoji_replacer.emoji")) {
                emojiSequences.put(characters, configFile.getString("message_replacers.emoji_replacer.emoji." + characters));
            }
            filters.add(new EmojiReplacer(emojiSequences));
        }

        return filters;
    }

    /**
     * Returns a {@link java.util.Map} of servers to the default channel to enforce on that server
     *
     * @param configFile The proxy {@link ConfigFile}
     * @return {@link java.util.Map} of servers and their default channels
     */
    private static HashMap<String, String> getServerDefaultChannels(ConfigFile configFile) {
        final HashMap<String, String> serverDefaults = new HashMap<>();
        if (configFile.contains("server_default_channels")) {
            for (String server : configFile.getConfigKeys("server_default_channels")) {
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
        for (ChatFilter chatFilter : chatFilters) {
            if (chatFilter instanceof ProfanityFilterer p) {
                p.dispose();
                break;
            }
        }
        chatFilters = new ArrayList<>();
    }

    /**
     * Returns the aliases from a list of aliases
     *
     * @param aliases The alias list
     * @return The actual command aliases
     */
    public static String[] getAliases(List<String> aliases) {
        String[] aliasList = new String[aliases.size() - 1];
        for (int i = 1; i < aliases.size(); i++) {
            aliasList[i - 1] = aliases.get(i);
        }
        return aliasList;
    }
}
