package me.william278.huskchat.channels;

import de.themoep.minedown.MineDown;
import de.themoep.minedown.MineDownParser;
import me.william278.huskchat.messagedata.PlaceholderReplacer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.List;

public class Channel {
    private final String channelId;
    private final String chatFormat;
    private final Scope scope;
    private final HashSet<String> shortcutCommands = new HashSet<>();
    private String sendPermission;
    private String receivePermission;
    private final boolean logToConsole;
    private final boolean censor;
    private final boolean passThrough;

    public Channel(String channelId, String chatFormat, String broadcastType, boolean logToConsole, boolean censor, boolean passThrough) {
        this.channelId = channelId;
        this.chatFormat = chatFormat;
        this.scope = Scope.valueOf(broadcastType);
        this.logToConsole = logToConsole;
        this.censor = censor;
        this.passThrough = passThrough;
    }

    public BaseComponent[] getFormattedMessage(ProxiedPlayer sender, String message) {
        ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.append(new MineDown(PlaceholderReplacer.replace(sender, getChatFormat())).toComponent());
        if (sender.hasPermission("huskchat.formatted_chat")) {
            componentBuilder.append(new MineDown(message).disable(MineDownParser.Option.ADVANCED_FORMATTING).toComponent());
        } else {
            componentBuilder.append(message);
        }
        return componentBuilder.create();
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChatFormat() {
        return chatFormat;
    }

    public Scope getBroadcastType() {
        return scope;
    }

    public HashSet<String> getShortcutCommands() {
        return shortcutCommands;
    }

    public void setShortcutCommands(List<String> shortcutCommands) {
        this.shortcutCommands.clear();
        this.shortcutCommands.addAll(shortcutCommands);
    }

    public String getSendPermission() {
        return sendPermission;
    }

    public void setSendPermission(String sendPermission) {
        this.sendPermission = sendPermission;
    }

    public String getReceivePermission() {
        return receivePermission;
    }

    public void setReceivePermission(String receivePermission) {
        this.receivePermission = receivePermission;
    }

    public boolean isLogToConsole() {
        return logToConsole;
    }

    public boolean isCensor() {
        return censor;
    }

    public boolean isPassThrough() {
        return passThrough;
    }

    public enum Scope {
        GLOBAL,
        LOCAL
    }
}
