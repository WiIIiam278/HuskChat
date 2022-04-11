package net.william278.huskchat.command;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.player.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class HuskChatCommand extends CommandBase {

    private final static String PERMISSION = "huskchat.command.huskchat";
    private final static String[] COMMAND_TAB_ARGUMENTS = {"about", "reload"};

    private final String pluginInformation;

    public HuskChatCommand(HuskChat implementor) {
        super("huskchat", PERMISSION, implementor, "hc");
        this.pluginInformation = "[HuskChat](#00fb9a bold) [| " + implementor.getMetaPlatform() + " Version " + implementor.getMetaVersion() + "](#00fb9a)\n" +
                "[" + implementor.getMetaDescription() + "](gray)\n" +
                "[• Author:](white) [William278](gray show_text=&7Click to visit website open_url=https://william278.net)\n" +
                "[• Contributors:](white) [TrueWinter](gray show_text=&7Code)\n" +
                "[• Translators:](white) [xF3d3](gray show_text=&7Italian, it-it), [MalzSmith](gray show_text=&7Hungarian, hu-hu)\n" +
                "[• Help Wiki:](white) [[Link]](#00fb9a show_text=&7Click to open link open_url=https://github.com/WiIIiam278/HuskChat/wiki/)\n" +
                "[• Report Issues:](white) [[Link]](#00fb9a show_text=&7Click to open link open_url=https://github.com/WiIIiam278/HuskChat/issues)\n" +
                "[• Support Discord:](white) [[Link]](#00fb9a show_text=&7Click to join open_url=https://discord.gg/tVYhJfyDWG)";
    }

    @Override
    public void onExecute(Player player, String[] args) {
        boolean isConsole = player instanceof ConsolePlayer;
        if (player.hasPermission(permission) || isConsole) {
            if (args.length == 1) {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "about", "info" -> {
                        if (!isConsole) {
                            sendAboutInformation(player);
                        } else {
                            implementor.getLoggingAdapter().log(Level.INFO, pluginInformation);
                        }
                    }
                    case "reload" -> {
                        implementor.reloadSettings();
                        implementor.reloadMessages();

                        if (!isConsole) {
                            implementor.getMessageManager().sendCustomMessage(player, "[HuskChat](#00fb9a bold) &#00fb9a&| Reloaded config & message files.");
                        } else {
                            implementor.getLoggingAdapter().log(Level.INFO, "[HuskChat](#00fb9a bold) &#00fb9a&| Reloaded config & message files.");
                        }
                    }
                    default -> {
                        if (!isConsole) {
                            implementor.getMessageManager().sendMessage(player, "error_invalid_syntax", "/huskchat <about/reload>");
                        } else {
                            implementor.getLoggingAdapter().log(Level.INFO, implementor.getMessageManager().getRawMessage("error_invalid_syntax", "/huskchat <about/reload>"));
                        }
                    }
                }
            } else {
                if (!isConsole) {
                    sendAboutInformation(player);
                } else {
                    implementor.getLoggingAdapter().log(Level.INFO, pluginInformation);
                }
            }
        } else {
            implementor.getMessageManager().sendMessage(player, "error_no_permission");
        }
    }

    /**
     * Send the plugin information to the {@link Player}
     *
     * @param player The {@link Player} to send plugin information to
     */
    private void sendAboutInformation(Player player) {
        implementor.getMessageManager().sendCustomMessage(player, pluginInformation);
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        if (!player.hasPermission(permission)) {
            return Collections.emptyList();
        }
        if (args.length <= 1) {
            return Arrays.stream(COMMAND_TAB_ARGUMENTS).filter(val -> val.toLowerCase().startsWith((args.length >= 1) ? args[0].toLowerCase() : ""))
                    .sorted().collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

}
