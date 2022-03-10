package net.william278.huskchat.bukkit.listener;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class MessageListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
//        if (channel.equals("HuskChat")) {
//            PlaceholderAPI.setPlaceholders(player, new String(message));
//        }
    }
}
