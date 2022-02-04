package net.william278.huskchat.command;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class ShortcutCommand extends CommandBase {

    private final static String PERMISSION = "huskchat.command.channel";

    private final String channelId;

    public ShortcutCommand(String command, String channelId, HuskChat implementor) {
        super(command, PERMISSION, implementor);
        this.channelId = channelId;
    }

    @Override
    public void onExecute(Player player, String[] args) {
        if (args.length == 0) {
            PlayerCache.switchPlayerChannel(player, channelId, implementor.getMessageManager());
        } else {
            StringJoiner message = new StringJoiner(" ");
            for (String arg : args) {
                message.add(arg);
            }
            final String oldChannelId = PlayerCache.getPlayerChannel(player.getUuid());
            PlayerCache.setPlayerChannel(player.getUuid(), channelId);
            player.proxyChat(message.toString());
            PlayerCache.setPlayerChannel(player.getUuid(), oldChannelId);
        }
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }

    public void switchChannels(Player player, String channelID) {
        for (Channel channel : Settings.channels) {
            if (channel.id.equalsIgnoreCase(channelID)) {
                if (channel.sendPermission != null) {
                    if (!player.hasPermission(channel.sendPermission)) {
                        implementor.getMessageManager().sendMessage(player, "error_no_permission_send", channel.id);
                        return;
                    }
                }
                PlayerCache.setPlayerChannel(player.getUuid(), channel.id);
                implementor.getMessageManager().sendMessage(player, "channel_switched", channel.id);
                return;
            }
        }
        implementor.getMessageManager().sendMessage(player, "error_invalid_channel");
    }
}
