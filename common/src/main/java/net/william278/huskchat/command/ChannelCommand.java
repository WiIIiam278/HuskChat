package net.william278.huskchat.command;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ChannelCommand extends CommandBase {

    private final static String PERMISSION = "huskchat.command.channel";

    public ChannelCommand(HuskChat implementor) {
        super(Settings.channelCommandAliases.get(0), PERMISSION, implementor, Settings.getAliases(Settings.channelCommandAliases));
    }

    @Override
    public void onExecute(Player player, String[] args) {
        if (player instanceof ConsolePlayer) {
            implementor.getLoggingAdapter().log(Level.INFO, implementor.getMessageManager().getRawMessage("error_in_game_only"));
            return;
        }
        if (player.hasPermission(permission)) {
            if (args.length == 1) {
                PlayerCache.switchPlayerChannel(player, args[0], implementor.getMessageManager());
            } else {
                implementor.getMessageManager().sendMessage(player, "error_invalid_syntax", "/channel <channel>");
            }
        } else {
            implementor.getMessageManager().sendMessage(player, "error_no_permission");
        }
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        if (!player.hasPermission(permission)) {
            return Collections.emptyList();
        }
        if (args.length <= 1) {
            return getChannelsIdsWithSendPermission(player).stream().filter(val ->
                            val.toLowerCase().startsWith((args.length >= 1) ? args[0].toLowerCase() : ""))
                    .sorted().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public HashSet<String> getChannelsIdsWithSendPermission(Player player) {
        final HashSet<String> channelsWithPermission = new HashSet<>();
        for (Channel channel : Settings.channels) {
            if (channel.sendPermission != null) {
                if (!player.hasPermission(channel.sendPermission)) {
                    continue;
                }
            }
            channelsWithPermission.add(channel.id);
        }
        return channelsWithPermission;
    }

}
