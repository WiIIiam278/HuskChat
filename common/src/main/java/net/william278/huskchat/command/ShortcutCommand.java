package net.william278.huskchat.command;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.message.ChatMessage;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Level;

public class ShortcutCommand extends CommandBase {

    private final static String PERMISSION = "huskchat.command.channel";

    private final String channelId;

    public ShortcutCommand(String command, String channelId, HuskChat implementor) {
        super(command, PERMISSION, implementor);
        this.channelId = channelId;
    }

    @Override
    public void onExecute(Player player, String[] args) {
        if (player instanceof ConsolePlayer) {
            implementor.getLoggingAdapter().log(Level.INFO, implementor.getMessageManager().getRawMessage("error_in_game_only"));
            return;
        }
        if (args.length == 0) {
            PlayerCache.switchPlayerChannel(player, channelId, implementor.getMessageManager());
        } else {
            StringJoiner message = new StringJoiner(" ");
            for (String arg : args) {
                message.add(arg);
            }
            new ChatMessage(channelId, player, message.toString(), implementor).dispatch();
        }
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }

}
