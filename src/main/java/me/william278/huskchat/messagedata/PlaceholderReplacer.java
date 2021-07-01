package me.william278.huskchat.messagedata;

import me.william278.huskchat.HuskChat;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PlaceholderReplacer {

    public static String replace(ProxiedPlayer player, String message) {
        final HashMap<String, String> placeholders = new HashMap<>();

        // Player related placeholders
        placeholders.put("%name%", HuskChat.getPlayerDataGetter().getPlayerName(player));
        placeholders.put("%fullname%", HuskChat.getPlayerDataGetter().getPlayerFullName(player));
        placeholders.put("%prefix%", HuskChat.getPlayerDataGetter().getPlayerPrefix(player));
        placeholders.put("%suffix%", HuskChat.getPlayerDataGetter().getPlayerSuffix(player));
        placeholders.put("%ping%", Integer.toString(player.getPing()));
        placeholders.put("%uuid%", player.getUniqueId().toString());
        placeholders.put("%servername%", player.getServer().getInfo().getName());
        placeholders.put("%serverplayercount%", Integer.toString(player.getServer().getInfo().getPlayers().size()));

        // Time related placeholders
        Date date = new Date();
        placeholders.put("%timestamp%", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date));
        placeholders.put("%time%", new SimpleDateFormat("HH:mm:ss").format(date));
        placeholders.put("%short_time%", new SimpleDateFormat("HH:mm").format(date));
        placeholders.put("%date%", new SimpleDateFormat("yyyy/MM/dd").format(date));
        placeholders.put("%british_date%", new SimpleDateFormat("dd/MM/yyyy").format(date));
        placeholders.put("%day%", new SimpleDateFormat("dd").format(date));
        placeholders.put("%month%", new SimpleDateFormat("MM").format(date));
        placeholders.put("%year%", new SimpleDateFormat("yyyy").format(date));

        for (String placeholder : placeholders.keySet()) {
            final String replacement = placeholders.get(placeholder);
            message = message.replace(placeholder, replacement);
        }

        return message;
    }

}
