package net.william278.huskchat.command;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class SocialSpyCommand extends CommandBase {

    private final static String PERMISSION = "huskchat.command.socialspy";

    public SocialSpyCommand(HuskChat implementor) {
        super(Settings.socialSpyCommandAliases.get(0), PERMISSION, implementor, Settings.getAliases(Settings.socialSpyCommandAliases));
    }

    @Override
    public void onExecute(Player player, String[] args) {
        if (player instanceof ConsolePlayer) {
            implementor.getLoggingAdapter().log(Level.INFO, implementor.getMessageManager().getRawMessage("error_in_game_only"));
            return;
        }
        if (player.hasPermission(permission)) {
            if (args.length == 1) {
                PlayerCache.SpyColor color;
                Optional<PlayerCache.SpyColor> selectedColor = PlayerCache.SpyColor.getColor(args[0]);
                if (selectedColor.isPresent()) {
                    color = selectedColor.get();
                    PlayerCache.setSocialSpy(player, color);
                    implementor.getMessageManager().sendMessage(player, "social_spy_toggled_on_color",
                            color.colorCode, color.name().toLowerCase().replaceAll("_", " "));
                    return;
                }
            }
            if (!PlayerCache.isSocialSpying(player)) {
                PlayerCache.setSocialSpy(player);
                implementor.getMessageManager().sendMessage(player, "social_spy_toggled_on");
            } else {
                PlayerCache.removeSocialSpy(player);
                implementor.getMessageManager().sendMessage(player, "social_spy_toggled_off");
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
