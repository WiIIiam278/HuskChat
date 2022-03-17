package net.william278.huskchat.command;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.player.Player;

import java.util.List;

/**
 * Abstract, cross-platform representation of a plugin command
 */
public abstract class CommandBase {

    /**
     * Command string
     */
    public final String command;

    /**
     * Command permission node
     */
    public final String permission;

    /**
     * Command aliases
     */
    public final String[] aliases;

    /**
     * Instance of the proxy plugin implementor
     */
    public final HuskChat implementor;


    public CommandBase(String command, String permission, HuskChat implementingPlugin, String... aliases) {
        this.command = command;
        this.permission = permission;
        this.implementor = implementingPlugin;
        this.aliases = aliases;
    }

    /**
     * Fires when the command is executed
     *
     * @param player {@link Player} executing the command
     * @param args   Command arguments
     */
    public abstract void onExecute(Player player, String[] args);

    /**
     * What should be returned when the player attempts to TAB complete the command
     *
     * @param player {@link Player} doing the TAB completion
     * @param args   Current command arguments
     * @return List of String arguments to offer TAB suggestions
     */
    public abstract List<String> onTabComplete(Player player, String[] args);

}
