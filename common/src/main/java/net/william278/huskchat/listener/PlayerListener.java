package net.william278.huskchat.listener;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

public abstract class PlayerListener {

    /**
     * Handle a player switching server
     *
     * @param player      The player changing server
     * @param newServer   The name of the server they are changing to
     * @param implementor The implementing plugin
     */
    public final void handlePlayerSwitchServer(Player player, String newServer, HuskChat implementor) {
        if (Settings.serverDefaultChannels.containsKey(newServer)) {
            PlayerCache.switchPlayerChannel(player, Settings.serverDefaultChannels.get(newServer),
                    implementor.getMessageManager());
        } else {
            for (Channel channel : Settings.channels) {
                if (channel.id.equalsIgnoreCase(PlayerCache.getPlayerChannel(player.getUuid()))) {
                    for (String restrictedServer : channel.restrictedServers) {
                        if (restrictedServer.equalsIgnoreCase(newServer)) {
                            PlayerCache.switchPlayerChannel(player, Settings.defaultChannel,
                                    implementor.getMessageManager());
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

}