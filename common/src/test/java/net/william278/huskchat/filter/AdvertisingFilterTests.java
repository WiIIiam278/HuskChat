package net.william278.huskchat.filter;

import net.william278.huskchat.player.TestPlayer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdvertisingFilterTests {

    AdvertisingFilterer filterer = new AdvertisingFilterer();

    @Test
    public void testSentence() {
        Assertions.assertTrue(filterer.isAllowed(new TestPlayer(), "This is an example sentence!"));
    }

    @Test
    public void testFullUrl() {
        Assertions.assertFalse(filterer.isAllowed(new TestPlayer(), "https://william278.net"));
    }

    @Test
    public void testPartialUrl() {
        Assertions.assertFalse(filterer.isAllowed(new TestPlayer(), "william278.net"));
    }

    @Test
    public void testSubDomainUrl() {
        Assertions.assertFalse(filterer.isAllowed(new TestPlayer(), "example.william278.net"));
    }

    @Test
    public void testSubDomainUrlWithPort() {
        Assertions.assertFalse(filterer.isAllowed(new TestPlayer(), "william278.net:25565"));
    }

    @Test
    public void testTopLevelDomain() {
        Assertions.assertTrue(filterer.isAllowed(new TestPlayer(), ".net"));
    }

}
