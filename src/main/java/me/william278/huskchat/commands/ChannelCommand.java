package me.william278.huskchat.commands;

import me.william278.huskchat.HuskChat;
import me.william278.huskchat.MessageManager;
import me.william278.huskchat.channels.Channel;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ChannelCommand extends Command implements TabExecutor {

    private final static String PERMISSION = "huskchat.command.channel";

    public ChannelCommand() { super("channel", PERMISSION, "c", "chat"); }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer player) {
            if (args.length == 1) {
                switchChannels(player, args[0]);
            } else {
                MessageManager.sendMessage(player, "error_invalid_syntax", "/channel <channel>");
            }
        }
    }

    public static void switchChannels(ProxiedPlayer player, String channelID) {
        for (Channel channel : HuskChat.getConfig().getChannels()) {
            if (channel.getChannelId().equalsIgnoreCase(channelID)) {
                if (channel.getSendPermission() != null) {
                    if (!player.hasPermission(channel.getSendPermission())) {
                        MessageManager.sendMessage(player, "error_no_permission_send", channel.getChannelId());
                        return;
                    }
                }
                HuskChat.setPlayerChannel(player.getUniqueId(), channel.getChannelId());
                MessageManager.sendMessage(player, "channel_switched", channel.getChannelId());
                return;
            }
        }
        MessageManager.sendMessage(player, "error_invalid_channel");
    }

    public HashSet<String> getChannelsIdsWithSendPermission(ProxiedPlayer player) {
        final HashSet<String> channelsWithPermission = new HashSet<>();
        for (Channel channel : HuskChat.getConfig().getChannels()) {
            if (channel.getSendPermission() != null) {
                if (!player.hasPermission(channel.getSendPermission())) {
                    continue;
                }
            }
            channelsWithPermission.add(channel.getChannelId());
        }
        return channelsWithPermission;
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer player) {
            if (!player.hasPermission(PERMISSION)) {
                return Collections.emptyList();
            }
            if (args.length == 1) {
                return getChannelsIdsWithSendPermission(player).stream().filter(val -> val.startsWith(args[0]))
                        .sorted().collect(Collectors.toList());
            } else {
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }
}
