package net.william278.huskchat.filter;

import net.william278.huskchat.player.Player;

import java.util.regex.Pattern;

/**
 * A {@link ChatFilter} that filters against domain names
 */
public class AdvertisingFilterer extends ChatFilter {

    /**
     * Lifted from <a href=https://gist.github.com/dperini/729294>https://gist.github.com/dperini/729294</a>
     * Licensed under MIT 3.0
     */
    private final Pattern domainPattern = Pattern.compile(
            "^" +
                    // protocol identifier (optional)
                    // short syntax // still required
                    "(?:(?:(?:https?|ftp):)?//)?" +
                    // user:pass BasicAuth (optional)
                    "(?:\\S+(?::\\S*)?@)?" +
                    "(?:" +
                    // IP address exclusion
                    // private & local networks
                    "(?!(?:10|127)(?:\\.\\d{1,3}){3})" +
                    "(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})" +
                    "(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})" +
                    // IP address dotted notation octets
                    // excludes loop back network 0.0.0.0
                    // excludes reserved space >= 224.0.0.0
                    // excludes network & broadcast addresses
                    // (first & last IP address of each class)
                    "(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])" +
                    "(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}" +
                    "(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))" +
                    "|" +
                    // host & domain names, may end with dot
                    // can be replaced by the shortest alternative
                    // (?![-_])(?:[-\\w\\u00a1-\\uffff]{0,63}[^-_]\\.)+
                    "(?:" +
                    "(?:" +
                    "[a-z0-9\\u00a1-\\uffff]" +
                    "[a-z0-9\\u00a1-\\uffff_-]{0,62}" +
                    ")?" +
                    "[a-z0-9\\u00a1-\\uffff]\\." +
                    ")+" +
                    // TLD identifier name, may end with dot
                    "(?:[a-z\\u00a1-\\uffff]{2,}\\.?)" +
                    ")" +
                    // port number (optional)
                    "(?::\\d{2,5})?" +
                    // resource path (optional)
                    "(?:[/?#]\\S*)?" +
                    "$",
            Pattern.CASE_INSENSITIVE);

    @Override
    public boolean isAllowed(Player player, String message) {
        return !(domainPattern.matcher(message).matches());
    }

    @Override
    public String getFailureErrorMessageId() {
        return "error_chat_filter_advertising";
    }

    @Override
    public String getFilterIgnorePermission() {
        return "huskchat.ignore_filters.advertising";
    }

}
