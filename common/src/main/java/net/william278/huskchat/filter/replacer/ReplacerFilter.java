package net.william278.huskchat.filter.replacer;

import net.william278.huskchat.filter.ChatFilter;

/**
 * A special kind of {@link ChatFilter} that can modify the contents of a message
 */
public abstract class ReplacerFilter extends ChatFilter {

    /**
     * Replace the input message from the user
     *
     * @param message The input message
     * @return The output, replaced, message
     */
    public abstract String replace(String message);

}
