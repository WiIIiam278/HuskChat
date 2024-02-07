/*
 * This file is part of HuskChat, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.huskchat.filter;

import net.william278.huskchat.user.TestOnlineUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AsciiFilterTests {

    AsciiFilter asciiFilter = new AsciiFilter(new ChatFilter.FilterSettings());

    @Test
    public void testAsciiSentence() {
        Assertions.assertTrue(asciiFilter.isAllowed(new TestOnlineUser(), "This is a test sentence"));
    }

    @Test
    public void testAsciiSentenceWithMathematicalSymbolsAndPunctuation() {
        Assertions.assertTrue(asciiFilter.isAllowed(new TestOnlineUser(), "This is a (test) sentence with [mathematical symbols], like + - = != etc :-)"));
    }

    @Test
    public void testUnicodeSentence() {
        Assertions.assertFalse(asciiFilter.isAllowed(new TestOnlineUser(), "• This is a test sentence with ♣ UNICODE ♣ characters •"));
    }

}
