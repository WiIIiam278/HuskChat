package net.william278.huskchat.filter;

import net.william278.profanitycheckerapi.ProfanityChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProfanityFilterTests {

    @Test
    public void testProfanityFilter() {
        try (ProfanityChecker checker = new ProfanityChecker()) {
            Assertions.assertTrue(checker.isTextProfane("This is a fucking test sentence"));
        }
    }

    @Test
    public void testProfanityThreshold() {
        try (ProfanityChecker checker = new ProfanityChecker()) {
            Assertions.assertTrue(checker.getTextProfanityLikelihood("Fuck shit ass") > 0.95d);
        }
    }
}
