package net.william278.huskchat.config;

import net.william278.huskchat.channel.Channel;

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
            channel.censor = configFile.getBoolean("channels." + channelID + ".censor", false);

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
}
