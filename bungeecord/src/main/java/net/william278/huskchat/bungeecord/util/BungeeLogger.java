package net.william278.huskchat.bungeecord.util;

import net.william278.huskchat.bungeecord.HuskChatBungee;
import net.william278.huskchat.util.Logger;

import java.util.logging.Level;

public class BungeeLogger implements Logger {

    private static final HuskChatBungee plugin = HuskChatBungee.getInstance();
    private static BungeeLogger instance;

    public static BungeeLogger get() {
        if (instance == null) {
            instance = new BungeeLogger();
        }
        return instance;
    }

    private BungeeLogger() {
    }

    @Override
    public void log(Level level, String s, Exception e) {
        plugin.getLogger().log(level, s, e);
    }

    @Override
    public void log(Level level, String s) {
        plugin.getLogger().log(level, s);
    }

    @Override
    public void info(String s) {
        plugin.getLogger().info(s);
    }

    @Override
    public void severe(String s) {
        plugin.getLogger().severe(s);
    }

    @Override
    public void config(String s) {
        plugin.getLogger().config(s);
    }
}
