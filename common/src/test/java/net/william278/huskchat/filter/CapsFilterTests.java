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

public class CapsFilterTests {

    CapsFilter capsFilter50Percent = new CapsFilter(new CapsFilter.CapsFilterSettings(0.5));
    CapsFilter capsFilter25Percent = new CapsFilter(new CapsFilter.CapsFilterSettings(0.25));
    CapsFilter capsFilter100Percent = new CapsFilter(new CapsFilter.CapsFilterSettings(1.0));

    @Test
    public void testCapsFilter_50PercentCaps_FullCaps() {
        Assertions.assertFalse(capsFilter50Percent.isAllowed(new TestOnlineUser(), "THIS IS A TEST MESSAGE"));
    }

    @Test
    public void testCapsFilter_50PercentCaps_LowerCase() {
        Assertions.assertTrue(capsFilter50Percent.isAllowed(new TestOnlineUser(), "this is a test message"));
    }

    @Test
    public void testCapsFilter_50PercentCaps_HalfCaps() {
        Assertions.assertTrue(capsFilter50Percent.isAllowed(new TestOnlineUser(), "this is a TEST MESSAGE"));
    }

    @Test
    public void testCapsFilter_25PercentCaps_FullCaps() {
        Assertions.assertFalse(capsFilter25Percent.isAllowed(new TestOnlineUser(), "THIS IS A TEST MESSAGE"));
    }

    @Test
    public void testCapsFilter_25PercentCaps_LowerCase() {
        Assertions.assertTrue(capsFilter25Percent.isAllowed(new TestOnlineUser(), "this is a test message"));
    }

    @Test
    public void testCapsFilter_25PercentCaps_HalfCaps() {
        Assertions.assertFalse(capsFilter25Percent.isAllowed(new TestOnlineUser(), "this is a TEST MESSAGE"));
    }

    @Test
    public void testCapsFilter_100PercentCaps_FullCaps() {
        Assertions.assertTrue(capsFilter100Percent.isAllowed(new TestOnlineUser(), "THIS IS A TEST MESSAGE"));
    }

    @Test
    public void testCapsFilter_100PercentCaps_LowerCase() {
        Assertions.assertTrue(capsFilter100Percent.isAllowed(new TestOnlineUser(), "this is a test message"));
    }

    @Test
    public void testCapsFilter_100PercentCaps_HalfCaps() {
        Assertions.assertTrue(capsFilter100Percent.isAllowed(new TestOnlineUser(), "this is a TEST MESSAGE"));
    }

}
