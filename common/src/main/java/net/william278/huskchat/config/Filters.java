package net.william278.huskchat.config;

import de.exlll.configlib.Configuration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.william278.huskchat.filter.ChatFilter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for loading and storing Chat Filters
 */
@SuppressWarnings("FieldMayBeFinal")
@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Filters {

    static final String CONFIG_HEADER = """
            ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
            ┃      HuskChat - Channels     ┃
            ┃    Developed by William278   ┃
            ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
            ┣╸ Information: https://william278.net/project/huskchat/
            ┗╸ Channels Help: https://william278.net/docs/huskchat/channels/""";

    private Map<ChatFilter.Type, ChatFilter.FilterSettings> filters = new HashMap<>(Map.of(
            ChatFilter.Type.ADVERTISING, ChatFilter.Type.ADVERTISING.getDefaultSettings(),
            ChatFilter.Type.CAPS, ChatFilter.Type.CAPS.getDefaultSettings(),
            ChatFilter.Type.REPEAT, ChatFilter.Type.REPEAT.getDefaultSettings(),
            ChatFilter.Type.SPAM, ChatFilter.Type.SPAM.getDefaultSettings(),
            ChatFilter.Type.PROFANITY, ChatFilter.Type.PROFANITY.getDefaultSettings(),
            ChatFilter.Type.ASCII, ChatFilter.Type.ASCII.getDefaultSettings()
    ));

    private Map<ChatFilter.Type, ChatFilter.FilterSettings> replacers = new HashMap<>(Map.of(
            ChatFilter.Type.EMOJI, ChatFilter.Type.EMOJI.getDefaultSettings()
    ));

    public boolean isFilterEnabled(@NotNull ChatFilter.Type type) {
        return filters.get(type).isEnabled();
    }

    public boolean isReplacerEnabled(@NotNull ChatFilter.Type type) {
        return replacers.get(type).isEnabled();
    }


}
