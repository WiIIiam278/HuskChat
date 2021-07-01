package me.william278.huskchat.censor;

import me.william278.huskchat.HuskChat;
import net.md_5.bungee.api.ProxyServer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CensorUtil {

    public static String censor(String input) {
        final ArrayList<Pattern> swearFilterRegexes = new ArrayList<>();
        String output = input;

        try(InputStream inputStream = HuskChat.getInstance().getResourceAsStream("swear_filter.txt")) {
            String contents = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            for (String regex : contents.split("\n")) {
                swearFilterRegexes.add(Pattern.compile(regex));
            }
        } catch (IOException ignored) { }

        for (Pattern swearFilterer : swearFilterRegexes) {
            Matcher matcher = swearFilterer.matcher(output);
            if (matcher.find()) {
                ProxyServer.getInstance().getLogger().info("SWEAR!!!!");
                output = "************ THIS IS A CHRISTIAN MINECRAFT SERVER ****************";
            }
        }
        return output;
    }

}
