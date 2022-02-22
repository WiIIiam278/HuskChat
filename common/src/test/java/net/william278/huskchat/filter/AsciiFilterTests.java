package net.william278.huskchat.filter;

import net.william278.huskchat.player.TestPlayer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AsciiFilterTests {

    AsciiFilter asciiFilter = new AsciiFilter();

    @Test
    public void testAsciiSentence() {
        Assertions.assertTrue(asciiFilter.isAllowed(new TestPlayer(), "This is a test sentence"));
    }

    @Test
    public void testAsciiSentenceWithMathematicalSymbolsAndPunctuation() {
        Assertions.assertTrue(asciiFilter.isAllowed(new TestPlayer(), "This is a (test) sentence with [mathematical symbols], like + - = != etc :-)"));
    }

    @Test
    public void testUnicodeSentence() {
        Assertions.assertFalse(asciiFilter.isAllowed(new TestPlayer(), "• This is a test sentence with ♣ UNICODE ♣ characters •"));
    }

}
