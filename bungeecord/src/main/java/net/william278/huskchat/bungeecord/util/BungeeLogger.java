package net.william278.huskchat.bungeecord.util;

import de.themoep.minedown.MineDown;
import de.themoep.minedown.MineDownParser;
import net.william278.huskchat.bungeecord.HuskChatBungee;
import net.william278.huskchat.util.Logger;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        plugin.getLogger().log(level, stripMineDown(s), e);
    }

    @Override
    public void log(Level level, String s) {
        plugin.getLogger().log(level, stripMineDown(s));
    }

    @Override
    public void info(String s) {
        plugin.getLogger().info(stripMineDown(s));
    }

    @Override
    public void severe(String s) {
        plugin.getLogger().severe(stripMineDown(s));
    }

    @Override
    public void config(String s) {
        plugin.getLogger().config(stripMineDown(s));
    }

    private String stripMineDown(String message) {
        String out = "";
        String[] lines = message.split("\n");

        for (int i = 0; i < lines.length; i++) {
            out += handleMineDownLinks(lines[i]);
        }

        // This would work perfectly on its own, if there weren't links
        return MineDown.stringify(new MineDown(out)
                .filter(MineDownParser.Option.ADVANCED_FORMATTING)
                .filter(MineDownParser.Option.SIMPLE_FORMATTING)
                .filter(MineDownParser.Option.LEGACY_COLORS)
                .toComponent());
    }

    private String handleMineDownLinks(String string) {
        String out = "";
        // this regex extracts the text and url, only supports one link per line
        String regex = "[^\\[\\]\\(\\) ]*\\[([^\\(\\)]+)\\]\\([^\\(\\)]+open_url=(\\S+).*\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(string);

        if (m.find()) {
            // match 0 is the whole match, 1 is the text, 2 is the url
            out += string.replace(m.group(0), "");
            out += m.group(1) + ": " + m.group(2);
        } else {
            out += string;
        }

        out += "\n";
        return out;
    }
}
