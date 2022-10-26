package net.william278.huskchat.util;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.player.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PlaceholderReplacer {

    public static String replace(Player player, String message, HuskChat implementingPlugin) {
        final HashMap<String, String> placeholders = new HashMap<>();

        // Player related placeholders
        placeholders.put("%name%", escape(implementingPlugin.getDataGetter().getPlayerName(player)));
        placeholders.put("%fullname%", escape(implementingPlugin.getDataGetter().getPlayerFullName(player)));
        placeholders.put("%prefix%", implementingPlugin.getDataGetter().getPlayerPrefix(player).isPresent() ? implementingPlugin.getDataGetter().getPlayerPrefix(player).get() : "");
        placeholders.put("%suffix%", implementingPlugin.getDataGetter().getPlayerSuffix(player).isPresent() ? implementingPlugin.getDataGetter().getPlayerSuffix(player).get() : "");
        placeholders.put("%ping%", Integer.toString(player.getPing()));
        placeholders.put("%uuid%", player.getUuid().toString());
        placeholders.put("%servername%", Settings.serverNameReplacement.getOrDefault(
                        player.getServerName(), player.getServerName()
                ));
        placeholders.put("%serverplayercount%", Integer.toString(player.getPlayersOnServer()));

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

    public static String escape(String string) {
        // Just escaping __ should suffice as the only special character
        // allowed in Minecraft usernames is the underscore.
        // By placing the escape character in the middle, the MineDown
        // parser no longer sees this as a formatting code.
        return string.replace("__", "_\\_");
    }
}
