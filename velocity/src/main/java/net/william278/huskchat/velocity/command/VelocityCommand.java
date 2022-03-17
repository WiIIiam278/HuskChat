package net.william278.huskchat.velocity.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.william278.huskchat.command.CommandBase;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.velocity.HuskChatVelocity;
import net.william278.huskchat.velocity.player.VelocityPlayer;

import java.util.Collections;
import java.util.List;

public class VelocityCommand implements SimpleCommand {

    private static final HuskChatVelocity plugin = HuskChatVelocity.getInstance();

    private final CommandBase implementer;

    public VelocityCommand(CommandBase command) {
        this.implementer = command;
        plugin.getProxyServer().getCommandManager().register(command.command, this, command.aliases);
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.source() instanceof Player player) {
            implementer.onExecute(VelocityPlayer.adaptCrossPlatform(player), invocation.arguments());
        } else {
            implementer.onExecute(ConsolePlayer.adaptConsolePlayer(plugin), invocation.arguments());
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        if (invocation.source() instanceof Player player) {
            return implementer.onTabComplete(VelocityPlayer.adaptCrossPlatform(player), invocation.arguments());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission(implementer.permission);
    }
}