package net.william278.huskchat.velocity.util;

import de.themoep.minedown.adventure.MineDown;
import de.themoep.minedown.adventure.MineDownParser;
import net.william278.huskchat.util.Logger;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VelocityLogger implements Logger {

    private org.slf4j.Logger parent;

    private VelocityLogger() {
    }

    private static VelocityLogger instance;

    public static VelocityLogger get(org.slf4j.Logger parent) {
        if (instance == null) {
            instance = new VelocityLogger();
            instance.parent = parent;
        }
        return instance;
    }

    @Override
    public void log(Level level, String message, Exception e) {
        logMessage(level, message);
        e.printStackTrace();
    }

    @Override
    public void log(Level level, String message) {
        logMessage(level, message);
    }

    @Override
    public void info(String message) {
        logMessage(Level.INFO, message);
    }

    @Override
    public void severe(String message) {
        logMessage(Level.SEVERE, message);
    }

    @Override
    public void config(String message) {
        logMessage(Level.CONFIG, message);
    }

    // Logs the message using SLF4J
    private void logMessage(Level level, String message) {
        switch (level.intValue()) {
            case 1000 -> parent.error(stripMineDown(message)); // Severe
            case 900 -> parent.warn(stripMineDown(message)); // Warning
            case 70 -> parent.warn("[Config] " + stripMineDown(message));
            default -> parent.info(stripMineDown(message));
        }
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