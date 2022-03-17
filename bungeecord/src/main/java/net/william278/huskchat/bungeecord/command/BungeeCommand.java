package net.william278.huskchat.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.william278.huskchat.bungeecord.HuskChatBungee;
import net.william278.huskchat.bungeecord.player.BungeePlayer;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.command.CommandBase;

import java.util.Collections;

public class BungeeCommand extends Command implements TabExecutor {

    private static final HuskChatBungee plugin = HuskChatBungee.getInstance();

    private final CommandBase implementer;

    public BungeeCommand(CommandBase command) {
        super(command.command, command.permission, command.aliases);
        this.implementer = command;
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer player) {
            implementer.onExecute(BungeePlayer.adaptCrossPlatform(player), args);
        } else {
            implementer.onExecute(ConsolePlayer.adaptConsolePlayer(plugin), args);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer player) {
            return implementer.onTabComplete(BungeePlayer.adaptCrossPlatform(player), args);
        }
        return Collections.emptyList();
    }
}
