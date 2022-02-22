package net.william278.huskchat.filter;

import net.william278.huskchat.player.TestPlayer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProfanityFilterTests {

    // 0.78 is the default in the plugin config
    private static final double TEST_TOLERANCE_THRESHOLD = 0.78;

    /*
     * Requires Python 3.8+, jep, alt-profanity check and a properly set java library path
     * that has the jep driver for the platform
     */
    ProfanityFilterer profanityChecker = new ProfanityFilterer(
            ProfanityFilterer.ProfanityFilterMode.TOLERANCE,
            TEST_TOLERANCE_THRESHOLD,
            "");

    @Test
    public void testObviousProfanity_SingleWord() {
        Assertions.assertFalse(profanityChecker.isAllowed(new TestPlayer(), "fuck"));
    }

    @Test
    public void testObviousProfanity_Sentence() {
        Assertions.assertFalse(profanityChecker.isAllowed(new TestPlayer(), "This sentence has fuck in it"));
    }

    @Test
    public void testLessObviousProfanity_SingleWord() {
        Assertions.assertFalse(profanityChecker.isAllowed(new TestPlayer(), "crap"));
    }

    @Test
    public void testLessObviousProfanity_Sentence() {
        Assertions.assertFalse(profanityChecker.isAllowed(new TestPlayer(), "This sentence is a bit crap"));
    }

    @Test
    public void testNoProfanity_SingleWord() {
        Assertions.assertTrue(profanityChecker.isAllowed(new TestPlayer(), "hello"));
    }

    @Test
    public void testNoProfanity_Sentence() {
        Assertions.assertTrue(profanityChecker.isAllowed(new TestPlayer(), "This sentence is profanity-free"));
    }

    // Further reading: https://en.wikipedia.org/wiki/Scunthorpe_problem
    @Test
    public void testScunthorpeProblem_SingleWord() {
        Assertions.assertTrue(profanityChecker.isAllowed(new TestPlayer(), "Scunthorpe"));
    }

    @Test
    public void testScunthorpeProblem_Sentence() {
        Assertions.assertTrue(profanityChecker.isAllowed(new TestPlayer(), "The industrial town of Scunthorpe, England"));
    }
}
