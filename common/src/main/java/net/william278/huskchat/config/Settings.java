package net.william278.huskchat.config;

import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.filter.AdvertisingFilterer;
import net.william278.huskchat.filter.CapsFilter;
import net.william278.huskchat.filter.ChatFilter;
import net.william278.huskchat.filter.ProfanityFilterer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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

    // Message command config
    public static boolean doMessageCommand;
    public static String inboundMessageFormat;
    public static String outboundMessageFormat;
    public static boolean logPrivateMessages;
    public static boolean censorPrivateMessages;
    public static String messageLogFormat;
    public static List<String> messageCommandRestrictedServers = new ArrayList<>();

    // Social spy
    public static boolean doSocialSpyCommand;
    public static String socialSpyFormat;

    // Local spy
    public static boolean doLocalSpyCommand;
    public static String localSpyFormat;
    public static List<String> excludedLocalSpyChannels = new ArrayList<>();

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
        channelLogFormat = configFile.getString("channel_log_format", "[CHAT] [%channel%] %sender%: %message%");
        channels.addAll(fetchChannels(configFile));
        serverDefaultChannels = getServerDefaultChannels(configFile);

        // Other options
        doMessageCommand = configFile.getBoolean("message_command.enabled", true);
        inboundMessageFormat = configFile.getString("message_command.format.inbound", "&#00fb9a&%name% &8→ &#00fb9a&You&8: &f");
        outboundMessageFormat = configFile.getString("message_command.format.outbound", "&#00fb9a&You &8→ &#00fb9a&%name%&8 &f");
        logPrivateMessages = configFile.getBoolean("messages_command.log_to_console", true);
        censorPrivateMessages = configFile.getBoolean("messages_command.censor", false);
        messageLogFormat = configFile.getString("messages_command.log_format", "[MSG] [%sender% -> %receiver%]: %message%");
        if (configFile.contains("channels.messages_command.restricted_servers")) {
            messageCommandRestrictedServers = configFile.getStringList("channels.messages_command.restricted_servers");
        }

        // Social spy
        doSocialSpyCommand = configFile.getBoolean("social_spy.enabled", true);
        socialSpyFormat = configFile.getString("social_spy.format", "&e[Spy] &7%sender% &8→ &7%receiever%:%spy_color% ");

        // Local spy
        doLocalSpyCommand = configFile.getBoolean("local_spy.enabled", true);
        localSpyFormat = configFile.getString("local_spy.format", "&e[Spy] &7[%channel%] %name%&8:%spy_color% ");
        if (configFile.contains("local_spy.excluded_local_channels")) {
            excludedLocalSpyChannels = configFile.getStringList("local_spy.excluded_local_channels");
        }

        // Chat filters
        clearChatFilters(); // Clear and dispose of any existing ProfanityChecker instances
        if (configFile.getBoolean("chat_filters.advertising_filter.enabled", true)) {
            chatFilters.add(new AdvertisingFilterer());
        }
        if (configFile.getBoolean("chat_filters.caps_filter.enabled", true)) {
            chatFilters.add(new CapsFilter(configFile.getDouble("chat_filters.caps_filter.max_caps_percentage", 0.4)));
        }
        if (configFile.getBoolean("chat_filters.profanity_filter.enabled", false)) {
            chatFilters.add(new ProfanityFilterer(ProfanityFilterer.ProfanityFilterMode.valueOf(
                    configFile.getString("chat_filters.profanity_filter.mode", "TOLERANCE").toUpperCase()),
                    configFile.getDouble("chat_filters.profanity_filter.tolerance", 0.78d)));
        }
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
            final String format = configFile.getString("channels." + channelID + ".format", "%fullname%&r: %message%");
            final String broadcastScope = configFile.getString("channels." + channelID + ".broadcast_scope", "GLOBAL").toUpperCase();
            Channel channel = new Channel(channelID.toLowerCase(), format, Channel.BroadcastScope.valueOf(broadcastScope));

            // Read shortcut commands
            if (configFile.contains("channels." + channelID + ".shortcut_commands")) {
                channel.shortcutCommands = configFile.getStringList("channels." + channelID + ".shortcut_commands");
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
}
