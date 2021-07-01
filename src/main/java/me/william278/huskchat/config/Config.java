package me.william278.huskchat.config;

import de.themoep.minedown.MineDown;
import de.themoep.minedown.MineDownParser;
import me.william278.huskchat.channels.Channel;
import me.william278.huskchat.messagedata.PlaceholderReplacer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class Config {

    final String language;

    // Channel config
    final String defaultChannel;
    final HashSet<Channel> channels = new HashSet<>();

    // Message command config
    final boolean doMessageCommand;
    final String inboundMessageFormat;
    final String outboundMessageFormat;
    final boolean logMessagesToConsole;
    final boolean censorPrivateMessages;

    public Config(Configuration config) {
        language = config.getString("language", "en-gb");

        defaultChannel = config.getString("default_channel", "global");
        fetchChannels(config);

        doMessageCommand = config.getBoolean("message_command.enabled", true);
        inboundMessageFormat = config.getString("message_command.format.inbound", "&#00fb9a&%name% &8→ &#00fb9a&You&8: &f");
        outboundMessageFormat = config.getString("message_command.format.outbound", "&#00fb9a&You &8→ &#00fb9a&%name%&8 &f");
        logMessagesToConsole = config.getBoolean("messages_command.log_to_console", true);
        censorPrivateMessages = config.getBoolean("messages_command.censor", false);
    }

    // Load channel information from config
    private void fetchChannels(Configuration config) {
        for (String channelID : config.getSection("channels").getKeys()) {
            final String format = config.getString("channels." + channelID + ".format", "%fullname%&r: %message%");
            final String broadcastScope = config.getString("channels." + channelID + ".broadcast_scope", "GLOBAL");
            List<String> shortcutCommands = new ArrayList<>();
            if (config.contains("channels." + channelID + ".shortcut_commands")) {
                shortcutCommands = config.getStringList("channels." + channelID + ".shortcut_commands");
            }
            final String sendPermission = config.getString("channels." + channelID + ".permissions.send", null);
            final String receivePermission = config.getString("channels." + channelID + ".permissions.receive", null);
            final boolean logToConsole = config.getBoolean("channels." + channelID + ".log_to_console", true);
            final boolean censor = config.getBoolean("channels." + channelID + ".censor", false);

            Channel channel = new Channel(channelID.toLowerCase(Locale.ROOT), format, broadcastScope, logToConsole, censor);
            if (sendPermission != null) {
                channel.setSendPermission(sendPermission);
            }
            if (receivePermission != null) {
                channel.setReceivePermission(receivePermission);
            }
            if (!shortcutCommands.isEmpty()) {
                channel.setShortcutCommands(shortcutCommands);
            }
            channels.add(channel);
        }
    }

    public String getLanguage() {
        return language;
    }

    public String getDefaultChannel() {
        return defaultChannel;
    }

    public HashSet<Channel> getChannels() {
        return channels;
    }

    public boolean doMessageCommand() {
        return doMessageCommand;
    }

    public boolean isCensorPrivateMessages() {
        return censorPrivateMessages;
    }

    public boolean isLogMessagesToConsole() {
        return logMessagesToConsole;
    }

    public String getOutboundMessageFormat() {
        return outboundMessageFormat;
    }

    public BaseComponent[] getFormattedOutboundPrivateMessage(ProxiedPlayer targetPlayer, ProxiedPlayer sender, String message) {
        ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.append(new MineDown(PlaceholderReplacer.replace(targetPlayer, getOutboundMessageFormat())).toComponent());
        if (sender.hasPermission("huskchat.formatted_chat")) {
            componentBuilder.append(new MineDown(message).disable(MineDownParser.Option.ADVANCED_FORMATTING).toComponent());
        } else {
            componentBuilder.append(message);
        }
        return componentBuilder.create();
    }

    public String getInboundMessageFormat() {
        return inboundMessageFormat;
    }

    public BaseComponent[] getFormattedInboundPrivateMessage(ProxiedPlayer sender, String message) {
        ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.append(new MineDown(PlaceholderReplacer.replace(sender, getInboundMessageFormat())).toComponent());
        if (sender.hasPermission("huskchat.formatted_chat")) {
            componentBuilder.append(new MineDown(message).disable(MineDownParser.Option.ADVANCED_FORMATTING).toComponent());
        } else {
            componentBuilder.append(message);
        }
        return componentBuilder.create();
    }
}
