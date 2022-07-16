package net.william278.huskchat.command;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.message.BroadcastMessage;
import net.william278.huskchat.player.Player;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class BroadcastCommand extends CommandBase {

    private final static String PERMISSION = "huskchat.command.broadcast";

    public BroadcastCommand(HuskChat implementor) {
        super(Settings.broadcastCommandAliases.get(0), PERMISSION, implementor, Settings.getAliases(Settings.broadcastCommandAliases));
    }

    @Override
    public void onExecute(Player player, String[] args) {
        if (player.hasPermission(permission)) {
            if (args.length >= 1) {
                StringJoiner message = new StringJoiner(" ");
                for (String argument : args) {
                    message.add(argument);
                }
                new BroadcastMessage(player, message.toString(), implementor).dispatch();
            } else {
                implementor.getMessageManager().sendMessage(player, "error_invalid_syntax", "/broadcast <message>");
            }
        } else {
            implementor.getMessageManager().sendMessage(player, "error_no_permission");
        }
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }
}
