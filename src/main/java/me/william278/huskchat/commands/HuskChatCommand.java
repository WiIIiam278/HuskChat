package me.william278.huskchat.commands;

import de.themoep.minedown.MineDown;
import me.william278.huskchat.HuskChat;
import me.william278.huskchat.MessageManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;
import java.util.stream.Collectors;

public class HuskChatCommand extends Command implements TabExecutor {

    private static final HuskChat plugin = HuskChat.getInstance();
    private final static String[] COMMAND_TAB_ARGUMENTS = {"about", "reload"};
    private final static String PERMISSION = "huskchat.command.huskchat";
    private static final StringBuilder PLUGIN_INFORMATION = new StringBuilder()
            .append("[HuskChat](#00fb9a bold) [| Version ").append(plugin.getDescription().getVersion()).append("](#00fb9a)\n")
            .append("[").append(plugin.getDescription().getDescription()).append("](gray)\n")
            .append("[• Author:](white) [William278](gray show_text=&7Click to pay a visit open_url=https://youtube.com/William27528)\n")
            .append("[• Help Wiki:](white) [[Link]](#00fb9a show_text=&7Click to open link open_url=https://github.com/WiIIiam278/HuskChat/wiki/)\n")
            .append("[• Report Issues:](white) [[Link]](#00fb9a show_text=&7Click to open link open_url=https://github.com/WiIIiam278/HuskChat/issues)\n")
            .append("[• Support Discord:](white) [[Link]](#00fb9a show_text=&7Click to join open_url=https://discord.gg/tVYhJfyDWG)");


    public HuskChatCommand() { super("huskchat", PERMISSION); }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer player) {
            if (args.length == 1) {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "about", "info" -> sendAboutInformation(player);
                    case "reload" -> {
                        HuskChat.reloadConfig();
                        MessageManager.reloadMessages();
                        player.sendMessage(new MineDown("[HuskChat](#00fb9a bold) &#00fb9a&| Reloaded config & message files.").toComponent());
                    }
                    default -> MessageManager.sendMessage(player, "error_invalid_syntax", "/husktowns <about/reload>");
                }
            } else {
                sendAboutInformation(player);
            }
        }
    }

    private void sendAboutInformation(ProxiedPlayer player) {
        player.sendMessage(new MineDown(PLUGIN_INFORMATION.toString()).toComponent());
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer player) {
            if (!player.hasPermission(PERMISSION)) {
                return Collections.emptyList();
            }
            if (args.length == 1) {
                return Arrays.stream(COMMAND_TAB_ARGUMENTS).filter(val -> val.startsWith(args[0]))
                        .sorted().collect(Collectors.toList());
            } else {
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }
}
